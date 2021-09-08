/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.list;

import androidx.annotation.NonNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

public class ListUtils {

    public static <E> int getPosition(List<E> eList, Predicate<E> ePredicate) {
        if (eList != null && !eList.isEmpty()) {
            return Iterators.indexOf(eList.iterator(), ePredicate);
        }
        return -1;
    }

//    public static <E> int getPosition(PagedList<E> eList, Predicate<E> ePredicate) {
//        if (eList != null && !eList.isEmpty()) {
//            return Iterators.indexOf(eList.iterator(), ePredicate);
//        }
//        return -1;
//    }

    public static <E> boolean isDataExits(List<E> eList, Predicate<E> ePredicate) {
        int index = getPosition(eList, ePredicate);
        return index >= 0;
    }

    public static <E> E getDataPosition(List<E> eList, Predicate<E> ePredicate) {
        int index = getPosition(eList, ePredicate);
        if (index >= 0) {
            return eList.get(index);
        }
        return null;
    }

    public static <E> boolean remove(List<E> eList, Predicate<E> ePredicate) {
        if (eList != null && !eList.isEmpty()) {
            return Iterators.removeIf(eList.iterator(), ePredicate);
        }
        return false;
    }

    public static <E> boolean update(@Nullable E e, @Nullable List<E> eList, Predicate<E> ePredicate) {
        if (e != null && eList != null && !eList.isEmpty()) {
            int indext = Iterators.indexOf(eList.iterator(), ePredicate);
            if (indext >= 0) {
                eList.set(indext, e);
                return true;
            }
        }
        return false;
    }

    public static <E> boolean insertAndUpdate(@Nullable E e, @NonNull List<E> eList, IPredicate<E> ePredicate) {
        if (e != null) {
            int indext = Iterators.indexOf(eList.iterator(), ePredicate);
            if (indext >= 0) {
                eList.set(indext, e);
            } else {
                int insertTo = ePredicate.insertTo();
                if (insertTo == -1) {
                    eList.add(e);
                } else {
                    eList.add(insertTo, e);
                }
            }
            return true;
        }
        return false;
    }

    public static <E> boolean findAndUpdate(List<E> eList, MPredicate<E> ePredicate) {
        if (eList != null) {
            int indext = Iterators.indexOf(eList.iterator(), ePredicate);
            if (indext >= 0) {
                E e;
                if (ePredicate.isClone()) {
                    e = ePredicate.onChange(clone(eList.get(indext)));
                } else {
                    e = ePredicate.onChange(eList.get(indext));
                }
                if (e != null) {
                    eList.set(indext, e);
                    return true;
                }
            }
        }
        return false;
    }

    public static <E> int count(@Nullable List<E> eList, Predicate<E> ePredicate) {
        if (eList != null && !eList.isEmpty()) {
            Collection<E> filtered = Collections2.filter(eList, ePredicate);
            return filtered.size();
        }
        return 0;
    }

    @NonNull
    public static <E> List<E> filter(@Nullable List<E> eList, Predicate<E> ePredicate) {
        if (eList != null && !eList.isEmpty()) {
            Collection<E> filtered = Collections2.filter(eList, ePredicate);
            return new ArrayList<>(filtered);
        }
        return new ArrayList<>();
    }


    public static <E> E clone(E e) {
        try {
            Gson gson = new Gson();
            String data = gson.toJson(e);
            return gson.fromJson(data, (Type) e.getClass());
        } catch (Exception er) {

        }
        return null;
    }

    public interface IPredicate<E> extends Predicate<E> {
        default int insertTo() {
            return -1;
        }
    }

    public interface MPredicate<E> extends Predicate<E> {
        E onChange(E e);

        default boolean isClone() {
            return false;
        }
    }

}
