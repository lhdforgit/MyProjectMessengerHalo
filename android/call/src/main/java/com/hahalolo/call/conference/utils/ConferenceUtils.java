package com.hahalolo.call.conference.utils;

import com.hahalolo.call.conference.WsEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConferenceUtils {
   /** @deprecated */
   public static ArrayList<Integer> convertPublishersListToArray(List<Map<String, String>> publishersList) {
      ArrayList<Integer> publishersArray = new ArrayList(publishersList.size());
      Iterator var2 = publishersList.iterator();

      while(var2.hasNext()) {
         Map<String, String> element = (Map)var2.next();
         publishersArray.add(Integer.valueOf((String)element.get("id")));
      }

      return publishersArray;
   }

   public static ArrayList<Integer> convertPublishersToArray(List<WsEvent.Publisher> publishers) {
      ArrayList<Integer> publishersArray = new ArrayList(publishers.size());
      Iterator var2 = publishers.iterator();

      while(var2.hasNext()) {
         WsEvent.Publisher element = (WsEvent.Publisher)var2.next();
         publishersArray.add(element.id);
      }

      return publishersArray;
   }

   public static Map<Integer, Boolean> convertParticipantListToArray(List<Map<String, Object>> participants) {
      Map<Integer, Boolean> publishersMap = new HashMap(participants.size());
      Iterator var2 = participants.iterator();

      while(var2.hasNext()) {
         Map<String, Object> element = (Map)var2.next();
         Double idDouble = (Double)element.get("id");
         int id = idDouble.intValue();
         boolean isPublisher = (Boolean)element.get("publisher");
         publishersMap.put(id, isPublisher);
      }

      return publishersMap;
   }
}
