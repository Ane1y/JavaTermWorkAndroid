package ru.polytech.client;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.polytech.client.converters.DepartmentConverter;
import ru.polytech.client.converters.ProjectConverter;
import ru.polytech.client.entity.Departments;
import ru.polytech.client.entity.Projects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class ProjectActivity extends AppCompatActivity {

    private EditText ETId;
    private EditText ETName;
    private EditText ETCost;
    private Spinner spinnerDepartment;
    private EditText ETDateBeg;
    private EditText ETDateEnd;
    private EditText ETDateEndReal;
    private Button BPrev;
    private Button BNext;
    private TextView TVPage;
    private Button BNew;
    private Button BUpdate;
    private Button BAdd;
    private Button BDelete;
    private Button BFind;
    private EditText ETFindNameOrId;
    private Spinner spinner;
    private List<Projects> array;
    private String token;
    private String urlString;
    private int spinnerItem;
    private int currentRecord;
    private int arrayLength;
    private Departments currentDepartment;
    private CustomSpinnerAdapter<Departments> departmentAdapter;
    private RequestHandler handler;
    private ProjectConverter converter;
    private Gson projectGson;
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

        converter = new ProjectConverter();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(converter.getConverterClass(), converter);
        projectGson = builder.create();

        handler = new RequestHandler(urlString);
        handler.setToken(token);

        setContentView(R.layout.activity_project);

        ETId = findViewById(R.id.ETProjectId);
        ETName = findViewById(R.id.ETProjectName);
        ETCost = findViewById(R.id.ETProjectCost);
        spinnerDepartment = findViewById(R.id.SpProjectDepartmentSpinner);
        ETDateBeg = findViewById(R.id.ETProjectDateBeg);
        ETDateEnd = findViewById(R.id.ETProjectDateEnd);
        ETDateEndReal = findViewById(R.id.ETProjectDateEndReal);
        BPrev = findViewById(R.id.BProjectPrev);
        BNext = findViewById(R.id.BProjectNext);
        TVPage = findViewById(R.id.TVProjectPages);
        BNew = findViewById(R.id.BProjectNew);
        BUpdate = findViewById(R.id.BProjectUpdate);
        BAdd = findViewById(R.id.BProjectAdd);
        BDelete = findViewById(R.id.BProjectDelete);
        BFind = findViewById(R.id.BProjectFind);
        ETFindNameOrId = findViewById(R.id.ETProjectFindLastNameOrId);
        spinner = findViewById(R.id.SpProjectSpinner);
        ArrayAdapter<?> spAdapter =
                ArrayAdapter.createFromResource(this, R.array.ProjectFind, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spAdapter);

        if (!rolesList.contains("ROLE_ADMIN")) {
            BUpdate.setVisibility(View.GONE);
            BAdd.setVisibility(View.GONE);
            BDelete.setVisibility(View.GONE);
        }

        ETId.setFocusable(false);
        ETId.setLongClickable(false);
        ETId.setCursorVisible(false);
        ETDateBeg.setFocusable(false);
        ETDateBeg.setLongClickable(false);
        ETDateBeg.setCursorVisible(false);
        ETDateEnd.setFocusable(false);
        ETDateEnd.setLongClickable(false);
        ETDateEnd.setCursorVisible(false);
        ETDateEndReal.setFocusable(false);
        ETDateEndReal.setLongClickable(false);
        ETDateEndReal.setCursorVisible(false);

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

        updateDepartmentSpinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                switch (selectedItemPosition) {
                    case 0: {
                        ETFindNameOrId.setVisibility(View.GONE);
                        spinnerItem = 0;
                        break;
                    }
                  /*  case 1: {
                        ETFindNameOrId.setVisibility(View.GONE);
                        spinnerItem = 1;
                        break;
                    }*/
                    case 1: {
                        ETFindNameOrId.setVisibility(View.VISIBLE);
                        ETFindNameOrId.setInputType(TYPE_CLASS_NUMBER);
                        ETFindNameOrId.setHint("id");
                        spinnerItem = 1;
                        break;
                    }
                    case 2: {
                        ETFindNameOrId.setVisibility(View.VISIBLE);
                        ETFindNameOrId.setInputType(TYPE_CLASS_TEXT);
                        ETFindNameOrId.setHint("project name");
                        spinnerItem = 2;
                        break;
                    }
                    case 3: {
                        ETFindNameOrId.setVisibility(View.VISIBLE);
                        ETFindNameOrId.setInputType(TYPE_CLASS_TEXT);
                        ETFindNameOrId.setHint("department name");
                        spinnerItem = 3;
                        break;
                    }
                    case 4: {
                        ETFindNameOrId.setVisibility(View.VISIBLE);
                        ETFindNameOrId.setInputType(TYPE_CLASS_NUMBER);
                        ETFindNameOrId.setHint("department id");
                        spinnerItem = 4;
                        break;
                    }
                }
                ETFindNameOrId.setText("");
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                ETDateBeg.setText(getFormattedDate(year, month, day));
            }
        };

        ETDateBeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ProjectActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        final DatePickerDialog.OnDateSetListener mDateEndSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                ETDateEnd.setText(getFormattedDate(year, month, day));
            }
        };

        ETDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ProjectActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, mDateEndSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        final DatePickerDialog.OnDateSetListener mDateEndRealSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                ETDateEndReal.setText(getFormattedDate(year, month, day));
            }
        };

        ETDateEndReal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ProjectActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, mDateEndRealSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
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
                    setFieldsWithCurrentProject(currentRecord);
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
                    setFieldsWithCurrentProject(currentRecord);
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

    private String getFormattedDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd" , Locale.ROOT);
        return format1.format(cal.getTime());
    }

    private void updateDepartmentSpinner() {
        departmentAdapter.clear();
        CallBack<String> callBack = new CallBack<String>() {
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
        handler.setHttpMethod("GET");
        handler.setUrlResource("departments/all");
        handler.execute(callBack, null);
    }

    private void BFindClickListener() {
        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ResultConverter<Projects> converter = new ResultConverter<>(projectGson);
                Type listType = new TypeToken<List<Projects>>(){}.getType();
                Type type = new TypeToken<Projects>(){}.getType();
                List<Projects> list = converter.getListFromResult(result, listType, type);
                if (list.isEmpty()) {
                    createToast("Nothing found!");
                } else {
                    array = list;
                    currentRecord = 0;
                    arrayLength = array.size();
                    setFieldsWithCurrentProject(0);
                }
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "projects");
            }
        };

        String text = ETFindNameOrId.getText().toString();
        String url = "";
        switch (spinnerItem) {
            case 0: {
                url = "projects/all";
                break;
            }
            case 1: {
                url = "projects/getById/" + text;
                if (text.isEmpty()) {
                    createToast("not enough information!");
                    return;
                }
                break;
            }
            case 2: {
                url = "projects/getByName/" + text;
                if (text.isEmpty()) {
                    createToast("not enough information!");
                    return;
                }
                break;
            }
            case 3: {
                url = "projects/getByDepartmentName/" + text;
                if (text.isEmpty()) {
                    createToast("not enough information!");
                    return;
                }
                break;
            }
            case 4: {
                url = "projects/getByDepartmentId/" + text;
                if (text.isEmpty()) {
                    createToast("not enough information!");
                    return;
                }
                break;
            }
        }
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
                        setFieldsWithCurrentProject(currentRecord);
                    } else {
                        currentRecord--;
                        setFieldsWithCurrentProject(currentRecord);
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
                handlerForBadRequest(code,"projects");
            }
        };

        if (id.isEmpty()) {
            createToast("No Id");
        } else {
            String url = "projects/deleteById/" + id;
            handler.setUrlResource(url);
            handler.setHttpMethod("DELETE");
            handler.execute(callBack, null);
        }
    }

    private void BAddClickListener() {
        final String name = ETName.getText().toString();
        final String cost = ETCost.getText().toString();
        final String dateBeg = ETDateBeg.getText().toString();
        final String dateEnd = ETDateEnd.getText().toString();
        final String dateEndReal = ETDateEndReal.getText().toString();

        if (!ETId.getText().toString().isEmpty()) {
            createToast("Can't add. Please try to click 'New' for new one, or 'Update'");
            return;
        }

        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ResultConverter<Projects> converter = new ResultConverter<>(projectGson);
                Type listType = new TypeToken<List<Projects>>(){}.getType();
                Type type = new TypeToken<Projects>(){}.getType();
                List<Projects> list = converter.getListFromResult(result, listType, type);

                if (name.isEmpty() || cost.isEmpty() || dateBeg.isEmpty() || dateEnd.isEmpty() ||
                        dateEndReal.isEmpty() || currentDepartment == null) {
                    createToast("Not enough information!");
                    return;
                }
                Float newCost = Float.parseFloat(cost);
                Date beg = null;
                Date end = null;
                Date endReal = null;
                try {
                    java.util.Date utilDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse(dateBeg);
                    beg = new Date(utilDate.getTime());
                    utilDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse(dateEnd);
                    end = new Date(utilDate.getTime());
                    utilDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse(dateEndReal);
                    endReal = new Date(utilDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (end.getTime() < beg.getTime() || endReal.getTime() < beg.getTime()) {
                    createToast("wrong dates!");
                    return;
                }

                Projects object = new Projects(null, name, newCost, currentDepartment, beg, end, endReal);

                if (!list.contains(object)) {
                    CallBack<String> call = new CallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            final Projects proj = projectGson.fromJson(result, Projects.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ETId.setText(proj.getId().toString());
                                    arrayLength++;
                                    currentRecord = arrayLength - 1;
                                    array.add(proj);
                                    setFieldsWithCurrentProject(currentRecord);
                                }
                            });
                            createToast("Adding completed successfully!");
                        }

                        @Override
                        public void onFail(String message, int code) {
                            handlerForBadRequest(code, "projects");
                        }
                    };

                    String json = projectGson.toJson(object);
                    System.out.println(json);
                    String url = "projects/add";
                    handler.setUrlResource(url);
                    handler.setHttpMethod("POST");
                    handler.execute(call, json);
                } else {
                    createToast("Project already exist!");
                }

            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "departments");
            }
        };

        handler.setUrlResource("projects/all");
        handler.setHttpMethod("GET");
        handler.execute(callBack, null);
    }

    private void BUpdateClickListener() {
        String id = ETId.getText().toString();
        String name = ETName.getText().toString();
        String cost = ETCost.getText().toString();
        String dateBeg = ETDateBeg.getText().toString();
        String dateEnd = ETDateEnd.getText().toString();
        String dateEndReal = ETDateEndReal.getText().toString();

        CallBack<String> callBack = new CallBack<String>() {
            @Override
            public void onSuccess(String result) {
                Projects proj = projectGson.fromJson(result, Projects.class);
                Projects curr = array.get(currentRecord);
                curr.setId(proj.getId());
                curr.setName(proj.getName());
                curr.setCost(proj.getCost());
                curr.setDepartments(proj.getDepartments());
                curr.setDateBeg(proj.getDateBeg());
                curr.setDateEnd(proj.getDateEnd());
                curr.setDateEndReal(proj.getDateEndReal());
                createToast("Update completed successfully!");
            }

            @Override
            public void onFail(String message, int code) {
                handlerForBadRequest(code, "projects");
            }
        };

        if (id.isEmpty()) {
            createToast("No id!");
        } else {
            if (name.isEmpty() || cost.isEmpty() || dateBeg.isEmpty() || dateEnd.isEmpty() ||
                    dateEndReal.isEmpty() || currentDepartment == null) {
                createToast("Not enough information!");
            } else {
                Float newCost = Float.parseFloat(cost);
                Date beg = null;
                Date end = null;
                Date endReal = null;
                try {
                    java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateBeg);
                    beg = new Date(utilDate.getTime());
                    utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateEnd);
                    end = new Date(utilDate.getTime());
                    utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateEndReal);
                    endReal = new Date(utilDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (end.getTime() < beg.getTime() || endReal.getTime() < beg.getTime()) {
                    createToast("wrong dates!");
                    return;
                }

                Projects object = new Projects(Long.parseLong(id), name, newCost, currentDepartment, beg, end, endReal);
                String json = projectGson.toJson(object);
                String url = "projects/update";
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
        String mess;
        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND: {
                mess = "No such "+ name + "!";
                break;
            }
            case HttpURLConnection.HTTP_FORBIDDEN: {
                mess = "Your token is expired!";
                break;
            }
            default:
                mess = "Connection failed or token is expired!";
        }
        createToast(mess);
    }

    private void clearFields() {
        ETId.setText("");
        ETName.setText("");
        ETCost.setText("");
        ETDateBeg.setText("");
        ETDateEnd.setText("");
        ETDateEndReal.setText("");
    }

    private void clearFocuses() {
        ETId.clearFocus();
        ETName.clearFocus();
        ETCost.clearFocus();
        ETDateBeg.clearFocus();
        ETDateEnd.clearFocus();
        ETDateEndReal.clearFocus();
        ETFindNameOrId.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(BAdd.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setFieldsWithCurrentProject(final int num) {
        runOnUiThread(new Runnable() {
            public void run() {
                Projects object = array.get(num);
                ETId.setText(object.getId().toString());
                ETName.setText(object.getName());
                ETCost.setText(object.getCost().toString());
                List<Departments> list = departmentAdapter.getList();
                int ind = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals(object.getDepartments().getId())) {
                        ind = i;
                    }
                }
                spinnerDepartment.setSelection(ind);
                ETDateBeg.setText(object.getDateBeg().toString());
                ETDateEnd.setText(object.getDateEnd().toString());
                ETDateEndReal.setText(object.getDateEndReal().toString());
                TVPage.setText((num + 1) + "/" + arrayLength);
            }
        });
    }
}
