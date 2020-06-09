package ru.polytech.client;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
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

import ru.polytech.client.converters.DepartmentConverter;
import ru.polytech.client.converters.DepartmentsEmployeesConverter;
import ru.polytech.client.converters.EmployeeConverter;
import ru.polytech.client.entity.Departments;
import ru.polytech.client.entity.DepartmentsEmployees;
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

public class DepartmentsEmployeesActivity extends AppCompatActivity {

    private EditText ETId;
    private Spinner spinnerDepartment;
    private Spinner spinnerEmployee;
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
    private List<DepartmentsEmployees> array;
    private String token;
    private String urlString;
    private int spinnerItem;
    private int currentRecord;
    private int arrayLength;
    private Departments currentDepartment;
    private CustomSpinnerAdapter<Departments> departmentAdapter;
    private Employees currentEmployee;
    private CustomSpinnerAdapter<Employees> employeeAdapter;
    private RequestHandler handler;
    private DepartmentsEmployeesConverter converter;
    private Gson DEGson;
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

        converter = new DepartmentsEmployeesConverter();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(converter.getConverterClass(), converter);
        DEGson = builder.create();

        handler = new RequestHandler(urlString);
        handler.setToken(token);

        setContentView(R.layout.activity_departments_employees);

        ETId = findViewById(R.id.ETDEId);
        spinnerDepartment = findViewById(R.id.SpDEDepartmentSpinner);
        spinnerEmployee = findViewById(R.id.SpDEEmployeeSpinner);
        BPrev = findViewById(R.id.BDEPrev);
        BNext = findViewById(R.id.BDENext);
        TVPage = findViewById(R.id.TVDEPages);
        BNew = findViewById(R.id.BClearInfo);
        BUpdate = findViewById(R.id.BDEUpdate);
        BAdd = findViewById(R.id.BDEAdd);
        BDelete = findViewById(R.id.BDEDelete);
        BFind = findViewById(R.id.BDEFind);
        ETFindLastNameOrId = findViewById(R.id.ETDEFindLastNameOrId);
        ETFindFirstName = findViewById(R.id.ETDEFindFirstName);
        ETFindPatherName = findViewById(R.id.ETDEFindPatherName);
        spinner = findViewById(R.id.SpDESpinner);
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.DEFind, android.R.layout.simple_spinner_item);
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
                    case 3: {
                        ETFindLastNameOrId.setVisibility(View.VISIBLE);
                        ETFindLastNameOrId.setInputType(TYPE_CLASS_TEXT);
                        ETFindFirstName.setVisibility(View.GONE);
                        ETFindPatherName.setVisibility(View.GONE);
                        ETFindLastNameOrId.setHint("department name");
                        spinnerItem = 3;
                        break;
                    }
                    case 4: {
                        ETFindLastNameOrId.setVisibility(View.VISIBLE);
                        ETFindLastNameOrId.setInputType(TYPE_CLASS_NUMBER);
                        ETFindFirstName.setVisibility(View.GONE);
                        ETFindPatherName.setVisibility(View.GONE);
                        ETFindLastNameOrId.setHint("department id");
                        spinnerItem = 4;
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

        departmentAdapter = new CustomSpinnerAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDepartment = (Departments) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerDepartment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() ==  MotionEvent.ACTION_DOWN) {
                    updateDepartmentSpinner();
                }
                return false;
            }
        });

        employeeAdapter = new CustomSpinnerAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerEmployee.setAdapter(employeeAdapter);

        spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEmployee = (Employees) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerEmployee.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() ==  MotionEvent.ACTION_DOWN) {
                    updateEmployeeSpinner();
                }
                return false;
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
                    setFieldsWithCurrentDE(currentRecord);
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
                    setFieldsWithCurrentDE(currentRecord);
                }
                clearFocuses();
            }
        });

        BNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        updateDepartmentSpinner();
        updateEmployeeSpinner();
    }

    private void updateDepartmentSpinner() {
        departmentAdapter.clear();
        final CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                DepartmentConverter lConverter = new DepartmentConverter();
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(lConverter.getConverterClass(), lConverter);
                Gson departmentGson = builder.create();
                ResultConverter<Departments> converter = new ResultConverter<>(departmentGson);
                Type listType = new TypeToken<List<Departments>>(){}.getType();
                Type type = new TypeToken<Departments>(){}.getType();
                List<Departments> list = converter.getListFromResult(result, listType, type);
                if (!list.isEmpty()) {

                    final List<Departments> finalList = list;
                    System.out.println("departments: " + finalList.size());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            departmentAdapter.addAll(finalList);
                            departmentAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "departments");
            }
        };
        RequestHandler handl = new RequestHandler(urlString);
        handl.setToken(token);
        handl.setHttpMethod("GET");
        handl.setUrlResource("departments/all");
        handl.execute(callBack, null);
    }

    private void updateEmployeeSpinner() {
        employeeAdapter.clear();
        final CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                EmployeeConverter lConverter = new EmployeeConverter();
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(lConverter.getConverterClass(), lConverter);
                Gson employeeGson = builder.create();
                ResultConverter<Employees> converter = new ResultConverter<>(employeeGson);
                Type listType = new TypeToken<List<Employees>>(){}.getType();
                Type type = new TypeToken<Employees>(){}.getType();
                List<Employees> list = converter.getListFromResult(result, listType, type);
                if (!list.isEmpty()) {

                    final List<Employees> finalList = list;
                    System.out.println("employees: " + finalList.size());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            employeeAdapter.addAll(finalList);
                            employeeAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "employees");
            }
        };
        handler.setHttpMethod("GET");
        handler.setUrlResource("employees/all");
        handler.execute(callBack, null);
    }

    private void BFindClickListener() {
        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ResultConverter<DepartmentsEmployees> converter = new ResultConverter<>(DEGson);
                Type listType = new TypeToken<List<DepartmentsEmployees>>(){}.getType();
                Type type = new TypeToken<DepartmentsEmployees>(){}.getType();
                List<DepartmentsEmployees> list = converter.getListFromResult(result, listType, type);
                if (list.isEmpty()) {
                    createToast("Nothing found!");
                } else {
                    array = list;
                    currentRecord = 0;
                    arrayLength = array.size();
                    setFieldsWithCurrentDE(0);
                }
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "records");
            }
        };

        String url = "";
        String text = ETFindLastNameOrId.getText().toString();
        String first = ETFindFirstName.getText().toString();
        String pather = ETFindPatherName.getText().toString();
        switch (spinnerItem) {
            case 0: {
                url = "departmentsEmployees/all";
                break;
            }
            case 1: {
                url = "departmentsEmployees/getByEmployeeId/" + text;
                if (text.isEmpty())  {
                    createToast("Fill in all fields!");
                    return;
                }
                break;
            }
            case 2: {
                url = "departmentsEmployees/getByEmployeeName/" + text + "/" + first + "/" + pather;
                if (text.isEmpty() || first.isEmpty() || pather.isEmpty()) {
                    createToast("Fill in all fields!");
                    return;
                }
                break;
            }
            case 3: {
                url = "departmentsEmployees/getByDepartmentName/" + text;
                if (text.isEmpty()) {
                    createToast("Fill in all fields!");
                    return;
                }
                break;
            }
            case 4: {
                url = "departmentsEmployees/getByDepartmentId/" + text;
                if (text.isEmpty()) {
                    createToast("Fill in all fields!");
                    return;
                }
                break;
            }
        }
        if (spinnerItem != 0) {
            handler.setUrlResource(url);
            handler.setHttpMethod("GET");
            handler.execute(callBack, null);
        }
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
                        setFieldsWithCurrentDE(currentRecord);
                    } else {
                        currentRecord--;
                        setFieldsWithCurrentDE(currentRecord);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            clearFields();
                            TVPage.setText("0/0");
                        }
                    });
                }
                createToast("Record was deleted successfully!");
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "records");
            }
        };

        if (id.isEmpty()) {
            createToast("No Id");
        } else {
            String url = "departmentsEmployees/deleteById/" + id;
            handler.setUrlResource(url);
            handler.setHttpMethod("DELETE");
            handler.execute(callBack, null);
        }
    }

    private void BAddClickListener() {

        if (!ETId.getText().toString().isEmpty()) {
            createToast("Error was occurred. Try one more time");
            return;
        }

        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ResultConverter<DepartmentsEmployees> converter = new ResultConverter<>(DEGson);
                Type listType = new TypeToken<List<DepartmentsEmployees>>(){}.getType();
                Type type = new TypeToken<DepartmentsEmployees>(){}.getType();
                List<DepartmentsEmployees> list = converter.getListFromResult(result, listType, type);

                if (currentDepartment == null || currentEmployee == null) {
                    createToast("Fill in all fields!");
                    return;
                }
                DepartmentsEmployees object = new DepartmentsEmployees(null, currentDepartment, currentEmployee);

                if (!list.contains(object)) {
                    CallBack<String> call = new CallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            final DepartmentsEmployees de = DEGson.fromJson(result, DepartmentsEmployees.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ETId.setText(de.getId().toString());
                                    arrayLength++;
                                    currentRecord = arrayLength - 1;
                                    array.add(de);
                                    setFieldsWithCurrentDE(currentRecord);
                                }
                            });
                            createToast("Record was added successfully!");
                        }

                        @Override
                        public void onFail(String message, int code) {
                            handlerForBadRequest(code, "records");
                        }
                    };

                    String json = DEGson.toJson(object);
                    System.out.println(json);
                    String url = "departmentsEmployees/add";
                    handler.setUrlResource(url);
                    handler.setHttpMethod("POST");
                    handler.execute(call, json);
                } else {
                    createToast("Record have already existed!");
                }

            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "records");
            }
        };
        handler.setUrlResource("departmentsEmployees/all");
        handler.setHttpMethod("GET");
        handler.execute(callBack, null);
    }

    private void BUpdateClickListener() {
        String id = ETId.getText().toString();

        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                DepartmentsEmployees de = DEGson.fromJson(result, DepartmentsEmployees.class);
                DepartmentsEmployees curr = array.get(currentRecord);
                curr.setId(de.getId());
                curr.setDepartments(de.getDepartments());
                curr.setEmployees(de.getEmployees());

                createToast("Record was updated successfully!");
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "connection");
            }
        };

        if (id.isEmpty()) {
            createToast("No id!");
        } else {
            if (currentDepartment == null || currentEmployee == null) {
                createToast("Fill in all fields!");
            } else {
                DepartmentsEmployees object = new DepartmentsEmployees(Long.parseLong(id), currentDepartment, currentEmployee);
                String json = DEGson.toJson(object);
                String url = "departmentsEmployees/update";
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

    private void handlerForBadRequest(int code, String name) {
        String message;
        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND: {
                message = "No such " + name + "!";
                break;
            }
            case HttpURLConnection.HTTP_FORBIDDEN: {
                message = "Your token has been expired! Try to sign in one more time";
                break;
            }
            default:
                message = "Connection failed or token has been expired! Try to sign in one more time";
        }
        createToast(message);
    }

    private void clearFields() {
        ETId.setText("");
    }

    private void clearFocuses() {
        ETId.clearFocus();
        ETFindLastNameOrId.clearFocus();
        ETFindPatherName.clearFocus();
        ETFindFirstName.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(BAdd.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setFieldsWithCurrentDE(final int num) {
        runOnUiThread(new Runnable() {
            public void run() {
                DepartmentsEmployees object = array.get(num);
                ETId.setText(object.getId().toString());

                List<Departments> list1 = departmentAdapter.getList();
                int ind = -1;
                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i).getId().equals(object.getDepartments().getId())) {
                        ind = i;
                    }
                }
                spinnerDepartment.setSelection(ind);

                List<Employees> list2 = employeeAdapter.getList();
                ind = -1;
                for (int i = 0; i < list2.size(); i++) {
                    if (list2.get(i).getId().equals(object.getEmployees().getId())) {
                        ind = i;
                    }
                }
                spinnerEmployee.setSelection(ind);

                TVPage.setText((num + 1) + "/" + arrayLength);
            }
        });
    }
}
