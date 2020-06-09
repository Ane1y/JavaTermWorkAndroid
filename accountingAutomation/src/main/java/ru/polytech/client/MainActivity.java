package ru.polytech.client;


import android.annotation.SuppressLint;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button BEmployees;
    private Button BDepartments;
    private Button BProjects;
    private Button BDepartmentsEmployees;
    private Button BLogOut;
    private String token;
    private String urlString;
    private ArrayList<String> rolesList;
    private TextView userInfo;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle arguments = getIntent().getExtras();

        if (arguments != null) {
            token = arguments.getString("token");
            urlString = arguments.getString("url");
            rolesList = arguments.getStringArrayList("roles");
        }
        BEmployees = findViewById(R.id.BEmployees);
        BDepartments = findViewById(R.id.BDepartments);
        BProjects = findViewById(R.id.BProjects);
        BDepartmentsEmployees = findViewById(R.id.BDepartmentsEmployees);
        BLogOut = findViewById(R.id.BLogOut);
        userInfo = findViewById(R.id.userRole);

        if(rolesList.contains("ROLE_ADMIN")) {
            userInfo.setText("Admin");
        } else {
            userInfo.setText("User");
        }
        BLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        BEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmployees();
            }
        });
        BDepartments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepartments();
            }
        });
        BProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProjects();
            }
        });
        BDepartmentsEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepartmentsEmployees();
            }
        });
    }

    private void logOut() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void openEmployees() {
        Intent intent = new Intent(this, EmployeeActivity.class);
        startActivity(putExtraInIntent(intent));
    }

    private void openDepartments() {
        Intent intent = new Intent(this, DepartmentActivity.class);
        startActivity(putExtraInIntent(intent));
    }

    private void openProjects() {
        Intent intent = new Intent(this, ProjectActivity.class);
        startActivity(putExtraInIntent(intent));
    }

    private void openDepartmentsEmployees() {
        Intent intent = new Intent(this, DepartmentsEmployeesActivity.class);
        startActivity(putExtraInIntent(intent));
    }

    private Intent putExtraInIntent(Intent intent) {
        intent.putExtra("token", token);
        intent.putExtra("url", urlString);
        intent.putStringArrayListExtra("roles", rolesList);
        return intent;
    }

}
