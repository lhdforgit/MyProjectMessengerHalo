/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.jsoup.helper;

import com.halo.widget.jsoup.internal.StringUtil;
import com.halo.widget.jsoup.nodes.Attribute;
import com.halo.widget.jsoup.nodes.Attributes;
import com.halo.widget.jsoup.select.NodeTraversor;
import com.halo.widget.jsoup.select.NodeVisitor;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Helper class to transform a {@link com.halo.widget.jsoup.nodes.Document} to a {@link Document org.w3c.dom.Document},
 * for integration with toolsets that use the W3C DOM.
 */
public class W3CDom {
    protected DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    /**
     * Convert a jsoup Document to a W3C Document.
     * @param in jsoup doc
     * @return w3c doc
     */
    public Document fromJsoup(com.halo.widget.jsoup.nodes.Document in) {
        Validate.notNull(in);
        DocumentBuilder builder;
        try {
        	//set the factory to be namespace-aware
        	factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
            Document out = builder.newDocument();
            convert(in, out);
            return out;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Converts a jsoup document into the provided W3C Document. If required, you can set options on the output document
     * before converting.
     * @param in jsoup doc
     * @param out w3c doc
     * @see com.halo.widget.jsoup.helper.W3CDom#fromJsoup(com.halo.widget.jsoup.nodes.Document)
     */
    public void convert(com.halo.widget.jsoup.nodes.Document in, Document out) {
        if (!StringUtil.isBlank(in.location()))
            out.setDocumentURI(in.location());

        com.halo.widget.jsoup.nodes.Element rootEl = in.child(0); // skip the #root node
        NodeTraversor.traverse(new W3CBuilder(out), rootEl);
    }

    /**
     * Implements the conversion by walking the input.
     */
    protected static class W3CBuilder implements NodeVisitor {
        private static final String xmlnsKey = "xmlns";
        private static final String xmlnsPrefix = "xmlns:";

        private final Document doc;
        private final Stack<HashMap<String, String>> namespacesStack = new Stack<>(); // stack of namespaces, prefix => urn
        private Element dest;

        public W3CBuilder(Document doc) {
            this.doc = doc;
            this.namespacesStack.push(new HashMap<String, String>());
        }

        public void head(com.halo.widget.jsoup.nodes.Node source, int depth) {
            namespacesStack.push(new HashMap<>(namespacesStack.peek())); // inherit from above on the stack
            if (source instanceof com.halo.widget.jsoup.nodes.Element) {
                com.halo.widget.jsoup.nodes.Element sourceEl = (com.halo.widget.jsoup.nodes.Element) source;

                String prefix = updateNamespaces(sourceEl);
                String namespace = namespacesStack.peek().get(prefix);
                String tagName = sourceEl.tagName();

                Element el = namespace == null && tagName.contains(":") ?
                    doc.createElementNS("", tagName) : // doesn't have a real namespace defined
                    doc.createElementNS(namespace, tagName);
                copyAttributes(sourceEl, el);
                if (dest == null) { // sets up the root
                    doc.appendChild(el);
                } else {
                    dest.appendChild(el);
                }
                dest = el; // descend
            } else if (source instanceof com.halo.widget.jsoup.nodes.TextNode) {
                com.halo.widget.jsoup.nodes.TextNode sourceText = (com.halo.widget.jsoup.nodes.TextNode) source;
                Text text = doc.createTextNode(sourceText.getWholeText());
                dest.appendChild(text);
            } else if (source instanceof com.halo.widget.jsoup.nodes.Comment) {
                com.halo.widget.jsoup.nodes.Comment sourceComment = (com.halo.widget.jsoup.nodes.Comment) source;
                Comment comment = doc.createComment(sourceComment.getData());
                dest.appendChild(comment);
            } else if (source instanceof com.halo.widget.jsoup.nodes.DataNode) {
                com.halo.widget.jsoup.nodes.DataNode sourceData = (com.halo.widget.jsoup.nodes.DataNode) source;
                Text node = doc.createTextNode(sourceData.getWholeData());
                dest.appendChild(node);
            } else {
                // unhandled
            }
        }

        public void tail(com.halo.widget.jsoup.nodes.Node source, int depth) {
            if (source instanceof com.halo.widget.jsoup.nodes.Element && dest.getParentNode() instanceof Element) {
                dest = (Element) dest.getParentNode(); // undescend. cromulent.
            }
            namespacesStack.pop();
        }

        private void copyAttributes(com.halo.widget.jsoup.nodes.Node source, Element el) {
            for (Attribute attribute : source.attributes()) {
                // valid xml attribute names are: ^[a-zA-Z_:][-a-zA-Z0-9_:.]
                String key = attribute.getKey().replaceAll("[^-a-zA-Z0-9_:.]", "");
                if (key.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*"))
                    el.setAttribute(key, attribute.getValue());
            }
        }

        /**
         * Finds any namespaces defined in this element. Returns any tag prefix.
         */
        private String updateNamespaces(com.halo.widget.jsoup.nodes.Element el) {
            // scan the element for namespace declarations
            // like: xmlns="blah" or xmlns:prefix="blah"
            Attributes attributes = el.attributes();
            for (Attribute attr : attributes) {
                String key = attr.getKey();
                String prefix;
                if (key.equals(xmlnsKey)) {
                    prefix = "";
                } else if (key.startsWith(xmlnsPrefix)) {
                    prefix = key.substring(xmlnsPrefix.length());
                } else {
                    continue;
                }
                namespacesStack.peek().put(prefix, attr.getValue());
            }

            // get the element prefix if any
            int pos = el.tagName().indexOf(":");
            return pos > 0 ? el.tagName().substring(0, pos) : "";
        }

    }

    /**
     * Serialize a W3C document to a String.
     * @param doc Document
     * @return Document as string
     */
    public String asString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }
}
