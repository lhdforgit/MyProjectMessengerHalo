-keep class com.hahalolo.messager.mqtt.model.** { *; }

-keep class com.hahalolo.messager.mqtt.model.** { *; }

-keepclassmembers class * extends com.hahalolo.messager.chatkit.commons.ViewHolder {
   public <init>(android.view.View);
}
-keep class com.hahalolo.messager.chatkit.** { *; }
-keep class * extends com.hahalolo.messager.chatkit.messages.MessageHolders$BaseMessageViewHolder {
     public <init>(android.view.View, java.lang.Object);
     public <init>(android.view.View);
 }
-keep class * extends com.hahalolo.messager.chatkit.dialogs.DialogsListAdapter$BaseDialogViewHolder {
     public <init>(android.view.View);
 }