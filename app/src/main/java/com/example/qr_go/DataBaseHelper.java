package com.example.qr_go;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qr_go.Actor.Player;
import com.example.qr_go.Adapters.QRCommentAdapter;
import com.example.qr_go.QR.QR;
import com.example.qr_go.QR.QRComment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// 
/**
 * Provides functions that help retrieve data from database
 */
public class DataBaseHelper {

    public ArrayList<QR> convertQRListFromDB(List<Map<String, Object>> qrList) {
        ArrayList<QR> result = new ArrayList<>();

        for(int i = 0; i < qrList.size(); i++) {
            Map<String, Object> currentInQRList = qrList.get(i);
            String name = (String)currentInQRList.get("name");
            String avatar = (String)currentInQRList.get("avatar");
            String qr_hash = (String)currentInQRList.get("qrHash");
            Long scoreLong = (Long)currentInQRList.get("score");
            int score = scoreLong.intValue();
            // dummy arraylist since we never use these
            ArrayList<QRComment> commentsList = new ArrayList<>();
            ArrayList<String> playerList = new ArrayList<>();

            QR currentQR = new QR(qr_hash, name, avatar, score, commentsList, playerList);
            result.add(currentQR);
        }

        return result;
    }

    public ArrayList<String> convertPlayerListFromDB(List<String> playerList) {
        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < playerList.size(); i++) {
            String currentInPlayerList = playerList.get(i);
            result.add(currentInPlayerList);
        }

        return result;
    }

    public ArrayList<QRComment> convertQRCommentListFromDB(List<Map<String, Object>> commentList) {
        ArrayList<QRComment> result = new ArrayList<>();

        for(int i = 0; i < commentList.size(); i++) {
            Map<String, Object> currentInCommentList = commentList.get(i);
            String comment = (String)currentInCommentList.get("comment");
            String commenter = (String)currentInCommentList.get("commenter");


            QRComment currentComment = new QRComment(comment, commenter);
            result.add(currentComment);
        }

        return result;
    }

    public void updateDB(QR qr) {
        // get database information
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = db.collection(QR.class.getSimpleName());

        // Create hashmap for data
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", qr.getName());
        data.put("qr_hash", qr.getQrHash());
        data.put("score", qr.getScore());
        data.put("avatar", qr.getAvatar());
        data.put("commentsList", qr.getCommentsList());
        data.put("playerList", qr.getPlayerList());
        data.put("latitude", qr.getLatitude());
        data.put("longitude",qr.getLongitude());
        data.put("photoURI", qr.getPhotoURI());
        // add data to database
        // document named after user the hash
        collectionReference
                .document(qr.getQrHash())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("updateDB()", "Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("updateDB()", "Data not added: " + e);
                    }
                });
    }

    /**
     * Updates firestone database with player's information. Document named after user device ID.
     */
    public void updateDB(Player player) {
        // get database information
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = db.collection(Player.class.getSimpleName());


        // Create hashmap for data
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", player.getUsername());
        data.put("contact", player.getContact());
        data.put("qrList", player.getQRList());
        data.put("rank", player.getRank());
        data.put("highestScore", player.getHighestScore());
        data.put("lowestScore", player.getLowestScore());
        data.put("totalScore", player.getTotalScore());
        data.put("theme", player.getTheme());

        // add data to database
        // document named after user deviceID
        collectionReference
                .document(player.getDeviceID())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("updateDB()", "Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("updateDB()", "Data not added: " + e);
                    }
                });
    }
}
