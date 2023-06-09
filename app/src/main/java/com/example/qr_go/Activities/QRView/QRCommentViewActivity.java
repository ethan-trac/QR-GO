package com.example.qr_go.Activities.QRView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_go.Actor.Player;
import com.example.qr_go.Adapters.QRCommentAdapter;
import com.example.qr_go.Coupon;
import com.example.qr_go.DataBaseHelper;
import com.example.qr_go.Interfaces.RecyclerViewInterface;
import com.example.qr_go.QR.QR;
import com.example.qr_go.QR.QRComment;
import com.example.qr_go.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents comment page for QR
 */
public class QRCommentViewActivity extends QRActivity implements RecyclerViewInterface {

    private Button backButton;
    private Button addCommentButton;
    private EditText addCommentField;
    private QRCommentAdapter commentAdapter;
    private RecyclerView commentListRecyclerView;
    private ArrayList<QRComment> commentDataList;
    private DataBaseHelper dbHelper;

    public QRCommentViewActivity() {
        dbHelper = new DataBaseHelper();
        commentDataList = new ArrayList<>();
        commentAdapter = new QRCommentAdapter(QRCommentViewActivity.this, commentDataList, QRCommentViewActivity.this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_view);

        getViews();

        commentListRecyclerView.setAdapter(commentAdapter);

        try {
            getInfoFromBundle();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        updateQRInfo();

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        commentListRecyclerView.addItemDecoration(divider);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // will stack between two activities.
//                Intent myIntent = new Intent(QRViewActivity.this, ProfileQRListViewActivity.class);
//                myIntent.putExtra("android_id", android_id);
//                QRViewActivity.this.startActivity(myIntent);
                finish();
            }
        });

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = addCommentField.getText().toString();

                if(comment.isEmpty()) {
                    Toast.makeText(QRCommentViewActivity.this, "Please write something.", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference collectionReference = db.collection(QR.class.getSimpleName());

                    // put data into class
                    collectionReference.document(qr_hash).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String name = (String)documentSnapshot.get("name");
                                    String avatar = (String)documentSnapshot.get("avatar");
                                    int score = ((Long)documentSnapshot.get("score")).intValue();
                                    ArrayList<QRComment> commentList = dbHelper.convertQRCommentListFromDB((List<Map<String, Object>>)documentSnapshot.get("commentsList"));
                                    ArrayList<String> playerList = dbHelper.convertPlayerListFromDB((List<String>)documentSnapshot.get("playerList"));

                                    QR qr = new QR(qr_hash, name , avatar, score,
                                            commentList,
                                            playerList);

                                    db.collection(Player.class.getSimpleName()).document(getDeviceId()).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    qr.addComment(comment, (String)documentSnapshot.get("username"));
                                                    commentDataList.add(new QRComment(comment, (String)documentSnapshot.get("username")));

                                                    db.collection(QR.class.getSimpleName()).document(qr_hash).update("commentsList", commentDataList);

                                                    commentAdapter.notifyDataSetChanged();
                                                }
                                            });
                                }
                            });
                    addCommentField.setText(null);
                }
            }
        });
    }

    /**
     * Gets views from fragment
     */
    private void getViews() {
        // get views from fragment
        this.backButton = findViewById(R.id.back_button);
        this.commentListRecyclerView = findViewById(R.id.comment_list);
        this.commentListRecyclerView.setLayoutManager(new LinearLayoutManager(QRCommentViewActivity.this));
        this.addCommentButton = findViewById(R.id.add_comment_button);
        this.addCommentField = findViewById(R.id.comment_input);
    }

    /**
     * Updates the QR information on screen
     */
    private void updateQRInfo() {

        getViews();

        // get database information
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection(QR.class.getSimpleName());

        // put data into class
        collectionReference.document(qr_hash).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = (String)documentSnapshot.get("name");
                        String avatar = (String)documentSnapshot.get("avatar");
                        int score = ((Long)documentSnapshot.get("score")).intValue();
                        ArrayList<QRComment> commentList = dbHelper.convertQRCommentListFromDB((List<Map<String, Object>>)documentSnapshot.get("commentsList"));


                        QR qr = new QR(qr_hash, name , avatar, score,
                                commentList,
                                new ArrayList<>());


                        // get comments
                        commentDataList.addAll(commentList);

                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * This returns the device's ID
     * @return
     *      Return the String ID
     */
    public String getDeviceId() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    /**
     * Deletes comment at i
     * @param i
     * Index of comment
     */
    @Override
    public void onItemClick(int i) {
        // get database information
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection(Player.class.getSimpleName());

        // put data into class
        collectionReference.document(getDeviceId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(commentDataList.get(i).getCommenter().equals((String)documentSnapshot.get("username"))) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(QRCommentViewActivity.this);

                            Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
                            builder.setTitle("Delete comment");

                            TextView messageText = new TextView(QRCommentViewActivity.this);
                            messageText.setTypeface(typeface);
                            messageText.setText("Are you sure you want to delete this comment?");
                            messageText.setPadding(20, 20, 20, 20);
                            messageText.setTextSize(15);
                            builder.setView(messageText);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int index) {
                                    db.collection(QR.class.getSimpleName()).document(qr_hash).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                    commentDataList.remove(i);

                                                    db.collection(QR.class.getSimpleName()).document(qr_hash).update("commentsList", commentDataList);



                                                    commentAdapter.notifyDataSetChanged();
                                                }
                                            });

                                    dialogInterface.dismiss();
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    }
                });
    }
}
