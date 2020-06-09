package ru.polytech.client;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.polytech.client.converters.EmployeeConverter;
import ru.polytech.client.entity.Employees;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class EmployeeActivity extends AppCompatActivity {

    private EditText ETId;
    private EditText ETLastName;
    private EditText ETFirstName;
    private EditText ETPatherName;
    private EditText ETPosition;
    private EditText ETSalary;
    private Button BPrev;
    private Button BNext;
    private TextView TVPage;
    private Button BNew;
    private Button BUpdate;
    private Button BAdd;
    private Button BDelete;
    private Button BFind;
    private EditText ETFindLastNameOrId;
    private EditText ETFindFirstName;
    private EditText ETFindPatherName;
    private Spinner spinner;
    private List<Employees> array;
    private String token;
    private String urlString;
    private int spinnerItem;
    private int currentRecord;
    private int arrayLength;
    private RequestHandler handler;
    private EmployeeConverter converter;
    private Gson employeeGson;
    private ArrayList<String> rolesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            System.out.println("null!");
            token = arguments.getString("token");
            urlString = arguments.getString("url");
            rolesList = arguments.getStringArrayList("roles");
        }

        array = new ArrayList<>();

        converter = new EmployeeConverter();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(converter.getConverterClass(), converter);
        employeeGson = builder.create();

        handler = new RequestHandler(urlString);
        handler.setToken(token);

        setContentView(R.layout.activity_employee);

        ETId = findViewById(R.id.ETEmployeeId);
        ETLastName = findViewById(R.id.ETEmployeeLastName);
        ETFirstName = findViewById(R.id.ETEmployeeFirstName);
        ETPatherName = findViewById(R.id.ETEmployeePatherName);
        ETPosition = findViewById(R.id.ETEmployeePosition);
        ETSalary = findViewById(R.id.ETEmployeeSalary);
        BPrev = findViewById(R.id.BEmployeePrev);
        BNext = findViewById(R.id.BEmployeeNext);
        TVPage = findViewById(R.id.TVEmployeePages);
        BNew = findViewById(R.id.BEmployeeClear);
        BUpdate = findViewById(R.id.BEmployeeUpdate);
        BAdd = findViewById(R.id.BEmployeeAdd);
        BDelete = findViewById(R.id.BEmployeeDelete);
        BFind = findViewById(R.id.BEmployeeFind);
        ETFindLastNameOrId = findViewById(R.id.ETEmployeeFindLastNameOrId);
        ETFindFirstName = findViewById(R.id.ETEmployeeFindFirstName);
        ETFindPatherName = findViewById(R.id.ETEmployeeFindPatherName);
        spinner = findViewById(R.id.SpEmployeeSpinner);
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.EmployeeFind, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (!rolesList.contains("ROLE_ADMIN")) {
            BUpdate.setVisibility(View.GONE);
            BAdd.setVisibility(View.GONE);
            BDelete.setVisibility(View.GONE);
        }

        ETId.setFocusable(false);
        ETId.setLongClickable(false);
        ETId.setCursorVisible(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                switch (selectedItemPosition) {
                 /*   case 0: {
                        ETFindLastNameOrId.setVisibility(View.GONE);
                        ETFindFirstName.setVisibility(View.GONE);
                        ETFindPatherName.setVisibility(View.GONE);
                        spinnerItem = 0;
                        break;
                    }*/
                    case 0: {
                        ETFindLastNameOrId.setVisibility(View.GONE);
                        ETFindFirstName.setVisibility(View.GONE);
                        ETFindPatherName.setVisibility(View.GONE);
                        spinnerItem = 0;
                        break;
                    }
                    case 1: {
                        ETFindLastNameOrId.setVisibility(View.VISIBLE);
                        ETFindLastNameOrId.setInputType(TYPE_CLASS_NUMBER);
                        ETFindFirstName.setVisibility(View.GONE);
                        ETFindPatherName.setVisibility(View.GONE);
                        ETFindLastNameOrId.setHint("id");
                        spinnerItem = 1;
                        break;
                    }
                    case 2: {
                        ETFindLastNameOrId.setVisibility(View.VISIBLE);
                        ETFindLastNameOrId.setInputType(TYPE_CLASS_TEXT);
                        ETFindFirstName.setVisibility(View.VISIBLE);
                        ETFindPatherName.setVisibility(View.VISIBLE);
                        ETFindLastNameOrId.setHint("last name");
                        spinnerItem = 2;
                        break;
                    }
                }
                ETFindLastNameOrId.setText("");
                ETFindFirstName.setText("");
                ETFindPatherName.setText("");
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        BFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BFindClickListener();
                clearFocuses();
            }
        });

        BAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BAddClickListener();
                clearFocuses();
            }
        });

        BDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BDeleteClickListener();
                clearFocuses();
            }
        });

        BUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BUpdateClickListener();
                clearFocuses();
            }
        });

        BPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!array.isEmpty()) {
                    if (currentRecord > 0) {
                        currentRecord--;
                    }
                    setFieldsWithCurrentEmployee(currentRecord);
                }
                clearFocuses();
            }
        });

        BNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!array.isEmpty()) {
                    if (currentRecord < arrayLength - 1) {
                        currentRecord++;
                    }
                    setFieldsWithCurrentEmployee(currentRecord);
                }
                clearFocuses();
            }
        });

        BNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
                clearFocuses();
            }
        });
    }

    private void BFindClickListener() {
        final CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ResultConverter<Employees> converter = new ResultConverter<>(employeeGson);
                Type listType = new TypeToken<List<Employees>>(){}.getType();
                Type type = new TypeToken<Employees>(){}.getType();
                List<Employees> list = converter.getListFromResult(result, listType, type);
                if (list.isEmpty()) {
                    createToast("Nothing was found!");
                } else {
                    array = list;
                    currentRecord = 0;
                    arrayLength = array.size();
                    setFieldsWithCurrentEmployee(0);
                }
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code);
            }
        };

        String url = "";

            switch (spinnerItem) {
            case 0: {
                url = "employees/all";
                break;
            }
            case 1: {
                String id = ETFindLastNameOrId.getText().toString();
                if (id.isEmpty()) {
                    createToast("no id!");
                    return;
                }
                url = "employees/getById/" + id;
                break;
            }
            case 2: {
                String last = ETFindLastNameOrId.getText().toString();
                String first = ETFindFirstName.getText().toString();
                String pather = ETFindPatherName.getText().toString();
                if (last.isEmpty() || first.isEmpty() || pather.isEmpty()) {
                    createToast("Make sure you write first, last and pather name!");
                    return;
                }
                url = "employees/getByName/" + last + "/" + first + "/" + pather;
                break;
            }
        }
            System.out.println(url);
            handler.setUrlResource(url);
            handler.setHttpMethod("GET");
            handler.execute(callBack, null);

    }

    private void BDeleteClickListener() {
        String id = ETId.getText().toString();

        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                arrayLength--;
                array.remove(currentRecord);
                if (arrayLength != 0) {
                    if (currentRecord < arrayLength) {
                        setFieldsWithCurrentEmployee(currentRecord);
                    } else {
                        currentRecord--;
                        setFieldsWithCurrentEmployee(currentRecord);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            clearFields();
                            TVPage.setText("0/0");
                        }
                    });
                }
                createToast("Deleted completed successfully!");
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code);
            }
        };

        if (id.isEmpty()) {
            createToast("Make sure you write id");
        } else {
            String url = "employees/deleteById/" + id;
            handler.setUrlResource(url);
            handler.setHttpMethod("DELETE");
            handler.execute(callBack, null);
        }
    }

    private void BAddClickListener() {
        final String last = ETLastName.getText().toString();
        final String first = ETFirstName.getText().toString();
        final String pather = ETPatherName.getText().toString();
        final String position = ETPosition.getText().toString();
        final String salary = ETSalary.getText().toString();

        if (!ETId.getText().toString().isEmpty()) {
            createToast("Error occurred. Try one more time!");
            return;
        }


        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ResultConverter<Employees> converter = new ResultConverter<>(employeeGson);
                Type listType = new TypeToken<List<Employees>>(){}.getType();
                Type type = new TypeToken<Employees>(){}.getType();
                List<Employees> list = converter.getListFromResult(result, listType, type);

                if (last.isEmpty() || first.isEmpty() || pather.isEmpty() || position.isEmpty() || salary.isEmpty()) {
                    createToast("Not enough information!");
                    return;
                }
                Employees employee = new Employees(null, first, last, pather, position, Float.parseFloat(salary));

                if (!list.contains(employee)) {
                    CallBack<String> call = new CallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            final Employees empl = employeeGson.fromJson(result, Employees.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ETId.setText(empl.getId().toString());
                                    arrayLength++;
                                    currentRecord = arrayLength - 1;
                                    array.add(empl);
                                    setFieldsWithCurrentEmployee(currentRecord);
                                }
                            });
                            createToast("Record was added successfully!");
                        }

                        @Override
                        public void onFail(String message, int code) {
                            handlerForBadRequest(code);
                        }
                    };

                    String json = employeeGson.toJson(employee);
                    String url = "employees/add";
                    handler.setUrlResource(url);
                    handler.setHttpMethod("POST");
                    handler.execute(call, json);
                } else {
                    createToast("Such employee has already exist!");
                }

            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code); //Adding failed
            }
        };

        handler.setUrlResource("employees/all");
        handler.setHttpMethod("GET");
        handler.execute(callBack, null);
    }

    private void BUpdateClickListener() {
        String id = ETId.getText().toString();
        String last = ETLastName.getText().toString();
        String first = ETFirstName.getText().toString();
        String pather = ETPatherName.getText().toString();
        String position = ETPosition.getText().toString();
        String salary = ETSalary.getText().toString();

        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                Employees empl = employeeGson.fromJson(result, Employees.class);
                Employees curr = array.get(currentRecord);
                curr.setId(empl.getId());
                curr.setFirstName(empl.getFirstName());
                curr.setLastName(empl.getLastName());
                curr.setPatherName(empl.getPatherName());
                curr.setPosition(empl.getPosition());
                curr.setSalary(empl.getSalary());
                createToast("Record was updated successfully!");
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code); //Updating failed
            }
        };

        if (id.isEmpty()) {
            createToast("No id!");
        } else {
            if (last.isEmpty() || first.isEmpty() || pather.isEmpty() || position.isEmpty() || salary.isEmpty()) {
                createToast("Not enough information!");
            } else {
                Employees object = new Employees(Long.parseLong(id), first, last, pather, position, Float.parseFloat(salary));
                String json = employeeGson.toJson(object);
                String url = "employees/update";
                handler.setUrlResource(url);
                handler.setHttpMethod("PUT");
                handler.execute(callBack, json);
            }
        }
    }

    private void createToast(final String text) {
        runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getApplicationContext(), text,  Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void handlerForBadRequest(int code) {
        String mess;
        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND: {
                mess = "No such employees!";
                break;
            }
            case HttpURLConnection.HTTP_FORBIDDEN: {
                mess = "Your token is expired! Try to log in one more time";
                break;
            }
            case HttpURLConnection.HTTP_BAD_METHOD: {
                mess = "You don't have access to delete employees!";
                break;
            }
            default:
                mess = "Connection failed or token is expired! Try to log in one more time!";
        }
        createToast(mess);
    }

    private void clearFields() {
        ETId.setText("");
        ETLastName.setText("");
        ETFirstName.setText("");
        ETPatherName.setText("");
        ETPosition.setText("");
        ETSalary.setText("");
    }

    private void clearFocuses() {
        ETId.clearFocus();
        ETLastName.clearFocus();
        ETFirstName.clearFocus();
        ETPatherName.clearFocus();
        ETPosition.clearFocus();
        ETSalary.clearFocus();
        ETFindLastNameOrId.clearFocus();
        ETFindPatherName.clearFocus();
        ETFindFirstName.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(BAdd.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setFieldsWithCurrentEmployee(final int num) {
        runOnUiThread(new Runnable() {
            public void run() {
                System.out.println(array.get(num).getClass());
                Employees object = array.get(num);
                ETId.setText(object.getId().toString());
                ETLastName.setText(object.getLastName());
                ETFirstName.setText(object.getFirstName());
                ETPatherName.setText(object.getPatherName());
                ETPosition.setText(object.getPosition());
                ETSalary.setText(object.getSalary().toString());
                TVPage.setText((num + 1) + "/" + arrayLength);
            }
        });
    }
}
