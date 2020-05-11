package com.example.ofood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CheckOutActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        setContentView(R.layout.activity_check_out_activity);

        db.collection("Cart").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        final List<Integer> amount = new ArrayList<>();
                        final List<String> name = new ArrayList<>();

                        Map<String, Object> map = document.getData();
                        if (map != null){
                            for(Map.Entry<String, Object> entry :map.entrySet()){
                                amount.add(Integer.parseInt(entry.getValue().toString()));
                                name.add(entry.getKey());
                            }
                        }
                        db.collection("Price").document("Vegetable").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()){
                                        List<Integer> price = new ArrayList<>();
                                        List<String> vege = new ArrayList<>();

                                        Map<String, Object> map = document.getData();
                                        if (map != null){
                                            for(Map.Entry<String, Object> entry :map.entrySet()){
                                                price.add(Integer.parseInt(entry.getValue().toString()));
                                                vege.add(entry.getKey());
                                            }
                                        }
                                        String id = db.collection("UsersData").document(userID).collection("Orders").document().getId();
                                        Map<String, Object> invoice_stat = new HashMap<>();
                                        invoice_stat.put("Status", "Check Out");
                                        db.collection("UsersData").document(userID).collection("Orders").document(id)
                                                .set(invoice_stat)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Added to Orders.");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding to Orders! Try again.", e);
                                                    }
                                                });

                                        int count=0;
                                        int count2=0;
                                        int harga = 0, total = 0;
                                        for (String s: name){
                                            for (String t: vege){
                                                if(s.equals(t)){
                                                    harga= price.get(count2) * amount.get(count);
                                                    Map<String, Object> invoice_item = new HashMap<>();
                                                    invoice_item.put(s, amount.get(count));
                                                    db.collection("UsersData").document(userID).collection("Orders").document(id)
                                                            .update(invoice_item)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "Added to chart.");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error adding to chart! Try again.", e);
                                                                }
                                                            });
                                                }
                                                count2+=1;
                                            }
                                            count2=0;
                                            total+=harga;
                                            count+=1;
                                        }

                                        Log.d("TAG", String.valueOf(total));
                                        Map<String, Object> item = new HashMap<>();
                                        item.put("Price", total);
                                        Map<String, Object> invoice_item = new HashMap<>();
                                        invoice_item.put("Price", total);
                                        db.collection("UsersData").document(userID).collection("Orders").document(id)
                                                .update(invoice_item)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Added to chart.");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding to chart! Try again.", e);
                                                    }
                                                });
                                        Map<String, Object> random = new HashMap<>();
                                        random.put("New", "0");

                                        db.collection("Cart").document(userID).delete();
                                        db.collection("Cart").document(userID)
                                                .set(random)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Added to cart.");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding to cart! Try again.", e);
                                                    }
                                                });


                                    }
                                }
                            }
                        });

                    }
                }
            }
        });



    }
}
