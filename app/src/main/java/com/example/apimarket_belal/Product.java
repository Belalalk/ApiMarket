package com.example.apimarket_belal;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Product extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    String titleData;
    String priceData;
    String Info;
    String ImageData;
    String SelectedDocumentID;

    TextView ProductInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ProductInfo = findViewById(R.id.productInfo);
        ProductInfo.setMovementMethod(new ScrollingMovementMethod());
        titleData = getIntent().getStringExtra("Title");
        priceData = getIntent().getStringExtra("Price");
        ImageData = getIntent().getStringExtra("Image");
        Info = getIntent().getStringExtra("Info");

        getAllProducts();


        Button deleteData = findViewById(R.id.deleteData);
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteProduct(SelectedDocumentID);
                }catch (Exception e){
                    Toast.makeText(Product.this,e.toString(),Toast.LENGTH_LONG).show();
                }
                getAllProducts();
            }
        });

    }

    private void deleteProduct(String dID) {

        database.collection("product").document(dID).delete();

    }


    public void getAllProducts() {
        database.collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String handle = "";
                        Spinner mySpinner = findViewById(R.id.document_Spinner);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Product.this, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                adapter.add(document.getId());
                             handle +=
                                     "Document ID: "+document.getId() + " \n\n"
                                             +"ProductName: "+ document.getData().get("Title").toString()+"\n\n"
                                             +"ProductDecription: " +document.getData().get("Info").toString()+"\n\n"
                                             +"Price: "+document.getData().get("Price").toString()+"\n"+"------------------------------\n";
                                Log.d("Document ID: ", document.getId() + " => " + document.getData());
                            }
                            ProductInfo.setText(handle);
                            mySpinner.setAdapter(adapter);
                            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    SelectedDocumentID = mySpinner.getSelectedItem().toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        } else {
                            Log.w("ERROR", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}