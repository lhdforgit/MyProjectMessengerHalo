//package com.halo.widget.googleplace
//
//import android.content.Context
//import android.graphics.Typeface
//import android.text.style.CharacterStyle
//import android.text.style.StyleSpan
//import com.google.android.gms.tasks.Task
//import com.google.android.gms.tasks.Tasks
//import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.AutocompletePrediction
//import com.google.android.libraries.places.api.model.AutocompleteSessionToken
//import com.google.android.libraries.places.api.model.Place
//import com.google.android.libraries.places.api.net.FetchPlaceRequest
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
//import com.halo.widget.common.Log
//import kotlinx.coroutines.*
//import java.util.*
//import java.util.concurrent.ExecutionException
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.TimeoutException
//
//
//class GooglePlaceUtils {
//    companion object {
//        suspend fun filterPredictionsPlace(
//            context: Context,
//            query: String
//        ) = coroutineScope {
//            //val listPlaces = mutableListOf<Place>()
//            if (!Places.isInitialized()) {
//                Places.initialize(
//                    context,
//                    "AIzaSyDGBUwDntnuFpVUPjqejyzI3uo59HF_0Wc",
//                    Locale.getDefault()
//                )
//            }
//            val token = AutocompleteSessionToken.newInstance()
//            val request = FindAutocompletePredictionsRequest.builder()
//                .setSessionToken(token)
//                .setQuery(query)
//                .build()
//            val placesClient = Places.createClient(context)
//
//            var listPrediction = mutableListOf<AutocompletePrediction>()
//            val requests = ArrayList<Deferred<FetchPlaceRequest?>>()
//            val ss = async {
//                placesClient.findAutocompletePredictions(request)
//                    .addOnSuccessListener { response ->
//                        response.autocompletePredictions.let { _list ->
////                            "_list :... ${_list.size} ".Log()
////                            listPrediction = _list
////                            listPrediction.forEach {
////                                requests.add(fetchPlace(placesClient, it.placeId))
////                            }
//
//                            _list.forEach { _item ->
//
//                                async {
//
//                                    val fields = listOf(
//                                        Place.Field.LAT_LNG,
//                                        Place.Field.ADDRESS_COMPONENTS,
//                                        Place.Field.ADDRESS,
//                                        Place.Field.PLUS_CODE,
//                                        Place.Field.TYPES,
//                                        Place.Field.UTC_OFFSET
//                                    )
//
//                                    val requestsPlace =
//                                        FetchPlaceRequest.builder(_item.placeId, fields).build()
//
//                                    var ss = placesClient.fetchPlace(requestsPlace)
//
//
//                                    //requests.add(ss)
////                                        .addOnSuccessListener { responsePlace ->
////                                            "responsePlace.place :.. ${responsePlace.place} ".Log()
////                                            responsePlace.place
////                                        }.addOnFailureListener { _exeption -> }
//
//
//                                }
//
//
//                            }
//                        }
//                    }
//            }
//
//            ss.await()
//            delay(200)
//            requests.awaitAll()
//
//        }
//    }
//}
//
////https://stackoverflow.com/questions/54668523/how-to-implement-google-places-autocomplete-programmatically
//fun getPredictions(
//    context: Context,
//    constraint: CharSequence
//): ArrayList<PlaceAutocomplete>? {
//
//
//    if (!Places.isInitialized()) {
//        Places.initialize(
//            context,
//            "AIzaSyDGBUwDntnuFpVUPjqejyzI3uo59HF_0Wc",
//            Locale.getDefault()
//        )
//    }
//    val placesClient = Places.createClient(context)
//    val resultList: ArrayList<PlaceAutocomplete> = ArrayList()
//
//    // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
//    // and once again when the user makes a selection (for example when calling fetchPlace()).
//    val token = AutocompleteSessionToken.newInstance()
//
//    //https://gist.github.com/graydon/11198540
//    // Use the builder to create a FindAutocompletePredictionsRequest.
//    val request =
//        FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
//            //.setLocationBias(bounds)
//            //.setCountry("BD")
//            //.setTypeFilter(TypeFilter.ADDRESS)
//            .setSessionToken(token)
//            .setQuery(constraint.toString())
//            .build()
//    val autocompletePredictions: Task<FindAutocompletePredictionsResponse> =
//        placesClient.findAutocompletePredictions(request)
//
//    // This method should have been called off the main UI thread. Block and wait for at most
//    // 60s for a result from the API.
//    try {
//        Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS)
//    } catch (e: ExecutionException) {
//        e.printStackTrace()
//    } catch (e: InterruptedException) {
//        e.printStackTrace()
//    } catch (e: TimeoutException) {
//        e.printStackTrace()
//    }
//    return if (autocompletePredictions.isSuccessful) {
//        val findAutocompletePredictionsResponse: FindAutocompletePredictionsResponse? =
//            autocompletePredictions.result
//        findAutocompletePredictionsResponse?.autocompletePredictions?.forEach { _prediction ->
//            //"_prediction :.. ${_prediction} ".Log()
//            resultList.add(
//                PlaceAutocomplete(
//                    _prediction.placeId,
//                    _prediction.getPrimaryText(StyleSpan(Typeface.NORMAL)).toString(),
//                    _prediction.getFullText(StyleSpan(Typeface.NORMAL)).toString()
//                )
//            )
//        }
//        resultList
//    } else {
//        resultList
//    }
//}
//
