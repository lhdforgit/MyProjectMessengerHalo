## Usage

#### Drag edge:
- LEFT
- RIGHT
- TOP
- BOTTOM

### Step-1

Create a `SwipeLayout`.

There are some things you should be known, when you are creating `SwipeLayout`.

1. The last child is the `SurfaceView`, other children are `BottomViews`. 
2. You'd better add the 'layout_gravity' attribute to the `BottomView` in your layout file. (usage like [`DrawerLayout`](http://developer.android.com/reference/android/support/v4/widget/DrawerLayout.html)).

For example:

```xml
<com.hahalolo.swipe.SwipeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="80dp">
    <!-- Bottom View Start-->
     <LinearLayout
        android:background="#66ddff00"
        android:id="@+id/bottom_wrapper"
        android:layout_width="160dp"
        android:weightSum="1"
        android:layout_height="match_parent">
        <!--What you want to show-->
    </LinearLayout>
    <!-- Bottom View End-->

    <!-- Surface View Start -->
     <LinearLayout
        android:padding="10dp"
        android:background="#ffffff"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!--What you want to show in SurfaceView-->
    </LinearLayout>
    <!-- Surface View End -->
</com.hahalolo.swipe.SwipeLayout>
```

### Step-2


Get `SwipeLayout` instance.

```java
SwipeLayout swipeLayout =  (SwipeLayout)findViewById(R.id.sample1);

//set show mode.
swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

//add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
swipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_wrapper));

swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
               //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
               //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
               //when user's hand released.
            }
        });
```


***


Well done! You have mastered the basic usage of `SwipeLayout`. If you want to use `SwipeLayout` in `ListView`, `GridView`, or some other class which extends from [`AdapterView`](http://developer.android.com/reference/android/widget/AdapterView.html), you should learn how to use `SwipeAdapter`.