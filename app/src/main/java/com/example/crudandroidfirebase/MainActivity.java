package com.example.crudandroidfirebase;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //initialize
    EditText editTextName, editTextEmail, editTextNumber;
    Button buttonAddUser;
    ListView listViewUsers;
    //a list to store all the User from firebase database
    List<User> Users;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // method for find ids of views
        findViews();

        // to maintian click listner of views
        initListner();
    }


    private void findViews() {
        //getRefrance for user table
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        listViewUsers = (ListView) findViewById(R.id.listViewUsers);
        buttonAddUser = (Button) findViewById(R.id.buttonAddUser);
        //list for store objects of user
        Users = new ArrayList<>();
    }

    private void initListner() {
        //adding an onclicklistener to button
        buttonAddUser.setOnClickListener(new View.OnClickListener()      {
            @Override
            public void onClick(View view) {
                //calling the method addUser()
                //the method is defined below
                //this method is actually performing the write operation
                addUser();
            }
        });


        // list item click listener
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User User = Users.get(i);
                CallUpdateAndDeleteDialog(User.getUserid(), User.getUsername(), User.getUseremail(), User.getUsermobileno());


            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous User list
                Users.clear();
                //getting all nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting User from firebase console
                    User User = postSnapshot.getValue(User.class);
                    //adding User to the list
                    Users.add(User);
                }
                //creating Userlist adapter
                UserList UserAdapter = new UserList(MainActivity.this, Users);
                //attaching adapter to the listview
                listViewUsers.setAdapter(UserAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void CallUpdateAndDeleteDialog(final String userid, String username, final String email, String monumber) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);
        //Access Dialog views
        final EditText updateTextname = (EditText) dialogView.findViewById(R.id.updateTextname);
        final EditText updateTextemail = (EditText) dialogView.findViewById(R.id.updateTextemail);
        final EditText updateTextmobileno = (EditText) dialogView.findViewById(R.id.updateTextmobileno);
        updateTextname.setText(username);
        updateTextemail.setText(email);
        updateTextmobileno.setText(monumber);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteUser);
        //username for set dialog title
        dialogBuilder.setTitle(username);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        // Click listener for Update data
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = updateTextname.getText().toString().trim();
                String email = updateTextemail.getText().toString().trim();
                String mobilenumber = updateTextmobileno.getText().toString().trim();
                //checking if the value is provided or not Here, you can Add More Validation as you required

                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(email)) {
                        if (!TextUtils.isEmpty(mobilenumber)) {
                            //Method for update data
                            updateUser(userid, name, email, mobilenumber);
                            b.dismiss();
                        }
                    }
                }

            }
        });

        // Click listener for Delete data
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Method for delete data
                deleteUser(userid);
                b.dismiss();
            }
        });
    }

    private boolean updateUser(String id, String name, String email, String mobilenumber) {
        //getting the specified User reference
        DatabaseReference UpdateReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        User User = new User(id, name, email, mobilenumber);
        //update  User  to firebase
        UpdateReference.setValue(User);
        Toast.makeText(getApplicationContext(), "User Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteUser(String id) {
        //getting the specified User reference
        DatabaseReference DeleteReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        //removing User
        DeleteReference.removeValue();
        Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_LONG).show();
        return true;
    }


    private void addUser() {

        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String mobilenumber = editTextNumber.getText().toString().trim();


        //checking if the value is provided or not Here, you can Add More Validation as you required

        if (!TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(email)) {
                if (!TextUtils.isEmpty(mobilenumber)) {

                    //it will create a unique id and we will use it as the Primary Key for our User
                    String id = databaseReference.push().getKey();
                    //creating an User Object
                    User User = new User(id, name, email, mobilenumber);
                    //Saving the User
                    databaseReference.child(id).setValue(User);

                    editTextName.setText("");
                    editTextNumber.setText("");
                    editTextEmail.setText("");
                    Toast.makeText(this, "User added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Please enter a mobilenumber", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please enter a Email", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}