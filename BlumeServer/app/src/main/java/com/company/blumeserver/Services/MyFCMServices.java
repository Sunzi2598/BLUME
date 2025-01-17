package com.company.blumeserver.Services;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.company.blumeserver.Common.Common;
import com.company.blumeserver.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String,String> dataRecv=remoteMessage.getData();
        if(dataRecv!=null){
            if(dataRecv.get(Common.NOTI_TITLE).equals("New Order")){

                Intent intent=new Intent(this, MainActivity.class);
                intent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER,true);//Use extra to detect is app open from notification
                Common.showNotification(this, new Random().nextInt(),dataRecv.get(Common.NOTI_TITLE),dataRecv.get(Common.NOTI_CONTENT),intent);
            }else
                Common.showNotification(this,new Random().nextInt(),
                    dataRecv.get(Common.NOTI_TITLE),
                    dataRecv.get(Common.NOTI_CONTENT),null);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Common.updateToken(this,s,true,false);
    }
}