package com.iadvigun.study;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iadvigun.study.utils.AlarmApi;
import com.iadvigun.study.utils.ProductAPI;
import com.iadvigun.study.utils.ShopApi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Button buttonAlarm;
    private Button buttonProd;
    private Button buttonShop;
    private Button buttonAddProduct;
    private Button buttonAddShop;
    private TextView textResult;
    private RecyclerView recyclerView;
    private AdapterProduct adapterProduct;
    private AdapterShop adapterShop;
    private ImageView shopiLogo;
    private TextView alarmMessage;
    private TextView coordinates;
    private List<Product> productListFromDB = new ArrayList<>();
    private List<Shop> shopListFromDB = new ArrayList<>();
    private List<Product> alarmListFromDB = new ArrayList<>();
    private List<Product> notUpdatedAlarmsInDB = new ArrayList<>();
    private List<Product> notDeletedProductsInDB = new ArrayList<>();
    private List<Product> notEditedProductsInDB = new ArrayList<>();
    private List<Product> notAddedProductsInDB = new ArrayList<>();
    private List<Shop> notDeletedShopsInDB = new ArrayList<>();
    private List<Shop> notEditedShopsInDB = new ArrayList<>();
    private List<Shop> notAddedShopInDB = new ArrayList<>();
    private Product addedProduct;
    private Shop addedShop;
    private Product editedProduct;
    private Product deletedProduct;
    private Shop editedShop;
    private Shop deletedShop;
    private double actualLatitude = 0;
    private double actualLongitude = 0;
    private Shop nearestShop;
    private SharedPreferences sPref;
    private NotificationManager notificationManager;
    private int NOTIFY_ID = 1;
    private String ChannelId = "channel_id";
    private Retrofit retrofit;
    private TextView listsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonAlarm = findViewById(R.id.search_button);
        buttonProd = findViewById(R.id.button2);
        buttonShop = findViewById(R.id.button3);
        shopiLogo = findViewById(R.id.imageShopiMain);
        textResult = findViewById(R.id.textView2);  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        buttonAddProduct = findViewById(R.id.button_additionProduct);
        buttonAddShop = findViewById(R.id.button_additionShop);
        alarmMessage = findViewById(R.id.tv_ALARM_Message);
        coordinates = findViewById(R.id.textCoord);
        listsView = findViewById(R.id.textLists);
        coordinates.setVisibility(View.INVISIBLE);
        listsView.setVisibility(View.INVISIBLE);
        textResult.setVisibility(View.INVISIBLE);


        createRecyclerView();
        initRetrofit();

        View.OnClickListener onClickListenerAlarms = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAlarm.setBackgroundColor(getResources().getColor(R.color.buttonOn));
                buttonProd.setBackgroundColor(getResources().getColor(R.color.buttonOf));
                buttonShop.setBackgroundColor(getResources().getColor(R.color.buttonOf));
                shopiLogo.setVisibility(View.INVISIBLE);
                buttonAddProduct.setVisibility(View.INVISIBLE);
                buttonAddShop.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                loadAlarmsFromPhoneMemory();
                adapterProduct.clearItems();
                adapterProduct.setAlarmOrListPr(1);
                adapterProduct.setItems(alarmListFromDB);
                recyclerView.setAdapter(adapterProduct);

            }
        };
        buttonAlarm.setOnClickListener(onClickListenerAlarms);


        View.OnClickListener onClickListenerProd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonProd.setBackgroundColor(getResources().getColor(R.color.buttonOn));
                buttonShop.setBackgroundColor(getResources().getColor(R.color.buttonOf));
                buttonAlarm.setBackgroundColor(getResources().getColor(R.color.buttonOf));
                shopiLogo.setVisibility(View.INVISIBLE);
                buttonAddShop.setVisibility(View.INVISIBLE);
                buttonAddProduct.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                loadProductsFromPhoneMemory();
                adapterProduct.clearItems();
                adapterProduct.setAlarmOrListPr(2);
                adapterProduct.setItems(productListFromDB);
                recyclerView.setAdapter(adapterProduct);


            }
        };
        buttonProd.setOnClickListener(onClickListenerProd);


        View.OnClickListener onClickListenerAddProd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(MainActivity.this, "You clicked on button Add Product",
                //        Toast.LENGTH_SHORT).show();

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_product_layout);
                dialog.setTitle("Add product");

                final EditText editTextName = dialog.findViewById(R.id.edit_name);
                final EditText editTextAmount = dialog.findViewById(R.id.edit_amount);
                final EditText editTextExpiration = dialog.findViewById(R.id.edit_expiration);
                Button dialogButtonAccept = (Button) dialog.findViewById(R.id.button_accept);
                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.button_cancel);
                Button dialogButtonPlusAmount = (Button) dialog.findViewById((R.id.button_plus_Amount));
                Button dialogButtonPlusExpiration = (Button) dialog.findViewById((R.id.button_plus_Expiration));
                Button dialogButtonMinusAmount = (Button) dialog.findViewById((R.id.button_minus_Amount));
                Button dialogButtonMinusExpiration = (Button) dialog.findViewById((R.id.button_minus_Expiration));
                Button dialogClose = (Button) dialog.findViewById(R.id.button_close);

                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialogButtonPlusAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputedAmount = editTextAmount.getText().toString();
                        if (inputedAmount.equals("")) {
                            inputedAmount = "0";
                        }
                        int inputedAm = Integer.parseInt(inputedAmount);
                        if (inputedAm < 0) {
                            inputedAm = 0;
                        }
                        inputedAm++;
                        editTextAmount.setText(String.valueOf(inputedAm));
                    }
                });

                dialogButtonPlusExpiration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputedExpiration = editTextExpiration.getText().toString();
                        if (inputedExpiration.equals("")) {
                            inputedExpiration = "0";
                        }
                        int inputedExp = Integer.parseInt(inputedExpiration);
                        if (inputedExp < 0) {
                            inputedExp = 0;
                        }
                        inputedExp++;
                        editTextExpiration.setText(String.valueOf(inputedExp));
                    }
                });

                dialogButtonMinusAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputedAmount = editTextAmount.getText().toString();
                        if (inputedAmount.equals("")) {
                            inputedAmount = "0";
                        }
                        int inputedAm = Integer.parseInt(inputedAmount);
                        if (inputedAm <= 0) {
                            inputedAm = 0;
                        } else {
                            inputedAm--;
                        }
                        editTextAmount.setText(String.valueOf(inputedAm));
                    }
                });

                dialogButtonMinusExpiration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputedExpiration = editTextExpiration.getText().toString();
                        if (inputedExpiration.equals("")) {
                            inputedExpiration = "0";
                        }
                        int inputedExp = Integer.parseInt(inputedExpiration);
                        if (inputedExp <= 0) {
                            inputedExp = 0;
                        } else {
                            inputedExp--;
                        }
                        editTextExpiration.setText(String.valueOf(inputedExp));
                    }
                });


                dialogButtonAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputtedName = editTextName.getText().toString();
                        String inputtedAmount = editTextAmount.getText().toString();
                        if (inputtedAmount.equals("")) {
                            inputtedAmount = "0";
                        }
                        String inputtedExpiration = editTextExpiration.getText().toString();
                        if (inputtedExpiration.equals("")) {
                            inputtedExpiration = "0";
                        }

                        addedProduct = new Product(inputtedName, Integer.parseInt(inputtedAmount),
                                Integer.parseInt(inputtedExpiration));

                        addProductToDB();
                        addedProduct.setOverdueDate("in process..");
                        productListFromDB.add(addedProduct);
                        adapterProduct.setItems(productListFromDB);
                        recyclerView.setAdapter(adapterProduct);
                        saveProductsToPhoneMemory();
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Added new product",
                                Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                getProductDataFromDB();
                            }
                        }).start();
                    }
                });
                dialog.show();
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        };
        buttonAddProduct.setOnClickListener(onClickListenerAddProd);


        View.OnClickListener onClickListenerShop = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonShop.setBackgroundColor(getResources().getColor(R.color.buttonOn));
                buttonProd.setBackgroundColor(getResources().getColor(R.color.buttonOf));
                buttonAlarm.setBackgroundColor(getResources().getColor(R.color.buttonOf));
                shopiLogo.setVisibility(View.INVISIBLE);
                buttonAddProduct.setVisibility(View.INVISIBLE);
                buttonAddShop.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                loadShopsFromPhoneMemory();
                adapterShop.clearItems();
                recyclerView.setAdapter(adapterShop);
                adapterShop.setItems(shopListFromDB);
                recyclerView.setAdapter(adapterShop);
            }
        };
        buttonShop.setOnClickListener(onClickListenerShop);

        buttonAddShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_shop_layout);
                dialog.setTitle("Add shop");

                final EditText editTextName = dialog.findViewById(R.id.edit_name_shop);
                final EditText editTextLatitude = dialog.findViewById(R.id.edit_latitude);
                final EditText editTextLongitude = dialog.findViewById(R.id.edit_longitude);
                final EditText editTextAreaSize = dialog.findViewById(R.id.edit_area_Size);
                Button dialogButtonAccept = (Button) dialog.findViewById(R.id.button_accept_shop);
                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.button_cancel_shop);
                Button dialogClose = (Button) dialog.findViewById(R.id.button_close_shop);

                dialog.show();

                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialogButtonAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Toast.makeText(MainActivity.this, "Addition method",
                         //       Toast.LENGTH_SHORT).show();
                        String inputedName = editTextName.getText().toString();
                        String inputedLatitude = editTextLatitude.getText().toString();
                        if (inputedLatitude.equals("")) {
                            inputedLatitude = "0";
                        }
                        String inputedLongitude = editTextLongitude.getText().toString();
                        if (inputedLongitude.equals("")) {
                            inputedLongitude = "0";
                        }
                        String inputedAreaSize = editTextAreaSize.getText().toString();
                        if (inputedAreaSize.equals("")) {
                            inputedAreaSize = "0";
                        }

                        addedShop = new Shop(inputedName, Float.parseFloat(inputedLatitude),
                                Float.parseFloat(inputedLongitude), Integer.parseInt(inputedAreaSize));
                        addShopToDB();
                        shopListFromDB.add(addedShop);
                        adapterShop.setItems(shopListFromDB);
                        recyclerView.setAdapter(adapterShop);
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Added new shop",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });

        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);


        loadAlarmsFromPhoneMemory();
        loadShopsFromPhoneMemory();
        loadProductsFromPhoneMemory();
        loadNotDeletedProductsFromPhoneMemory();
        loadNotEditedProductsFromPhoneMemory();
        loadNotDeletedShopsFromPhoneMemory();
        loadNotEditedShopsFromPhoneMemory();
        initRetrofit();
        updateAllDataFromDB();
        makeAlarmNearTheShop();
        DBupdateScannerEditedProduct();
        DBupdateScannerDeletedProduct();
        DBupdateScannerDeletedShop();
        DBupdateScannerEditedShop();
        scannerNotActualisedDataToDB();


    }

    public void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(ChannelId, ChannelId, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    public void createNotificationMessage() {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notBilder =
                new NotificationCompat.Builder(getApplicationContext(), ChannelId)
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Hello, it's SHOPI! ")
                        .setContentText("You are near the shop " + nearestShop.getName() + "! "
                                + "You can by your " + alarmListFromDB.size() + " overdue products here :)")
                        .setPriority(PRIORITY_HIGH);
        createChannelIfNeeded(notificationManager);
        notificationManager.notify(NOTIFY_ID, notBilder.build());
    }

    public void makeAlarmNearTheShop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (alarmListFromDB.size() == 0 && count == 0) {
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count++;
                    } else if (alarmListFromDB.size() == 0 && count == 1) {
                        try {
                            Thread.sleep(600000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (AreYouNearTheShop()) {
                           createNotificationMessage();
                        alarmMessage.setText("You are near the shop " + nearestShop.getName() + "!\n"
                                + "You can by your " + alarmListFromDB.size() +
                                " overdue products here :)");
                        try {
                            Thread.sleep(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        alarmMessage.setText("");
                        try {
                            Thread.sleep(6000000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public boolean AreYouNearTheShop() {

        if (theSmallestDistanceToShop() < Float.MAX_VALUE && theSmallestDistanceToShop() < (float) 1000 +
                ((float) nearestShop.getAreaSize() / 2)) {
            return true;
        } else
            return false;
    }


    public float theSmallestDistanceToShop() {
        float theSmallestDistance = Float.MAX_VALUE;
        if (shopListFromDB.size() > 0) {
            float[] result = new float[3];
            for (int i = 0; i < shopListFromDB.size(); i++) {
                Location.distanceBetween(actualLatitude, actualLongitude, shopListFromDB.get(i).getLatitude(),
                        shopListFromDB.get(i).getLongitude(), result);
                if (theSmallestDistance > result[0]) {
                    nearestShop = shopListFromDB.get(i);
                    theSmallestDistance = result[0];
                }
            }
        }
        return theSmallestDistance;
    }

    public void getAlarmDataFromDB() {
        AlarmApi alarmAPI = retrofit.create(AlarmApi.class);
        Call<List<Product>> call = alarmAPI.getAlarms();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Can t load alarms! ");
                    return;
                }
                List<Product> list = response.body();
                alarmListFromDB.clear();
                alarmListFromDB.addAll(list);
                saveAlarmsToPhoneMemory();
                textResult.append("Loaded alarms from DB");
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                textResult.setText(t.getMessage());
            }
        });
    }

    void getProductDataFromDB() {
        ProductAPI productAPI = retrofit.create(ProductAPI.class);
        Call<List<Product>> call = productAPI.getProduct();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Can't Load products!");
                    return;
                }
                List<Product> list = response.body();
                productListFromDB.clear();
                productListFromDB.addAll(list);
                textResult.setText("Loaded products from DB");
                saveProductsToPhoneMemory();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                textResult.setText(t.getMessage());
            }
        });
    }

    private void addProductToDB() {
        ProductAPI productApi = retrofit.create(ProductAPI.class);
        addedProduct.setOverdueDate(null);
        Call<Product> call = productApi.createPost(addedProduct);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Code" + response.code());
                    addedProduct.setOverdueDate("no connection..");
                    saveProductsToPhoneMemory();

                    return;
                }
                Product responsePr = response.body();
                textResult.setText("response: " + response.code());
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                textResult.setText("Failure add!");
            }
        });
    }

    public void upDateProductInDB() {
        ProductAPI productApi = retrofit.create(ProductAPI.class);
        editedProduct.setOverdueDate(null);
        Call<Product> call = productApi.createPUT(editedProduct.getId(), editedProduct);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Code" + response.code());
                    editedProduct.setOverdueDate("no connection..");
                    int indexPR = findProductIndexInList(productListFromDB, editedProduct);
                    if(indexPR >= 0){
                    productListFromDB.set(indexPR, editedProduct);
                    saveProductsToPhoneMemory();}

                    if (!notEditedProductsInDB.contains(editedProduct)) {
                        notEditedProductsInDB.add(editedProduct);
                        saveNotEditedProductsToPhoneMemory();
                    } else {
                        int index = findProductIndexInList(notEditedProductsInDB, editedProduct);
                        notEditedProductsInDB.set(index, editedProduct);
                        saveNotEditedProductsToPhoneMemory();
                    }
                    return;
                }
                Product responsePr = response.body();
                getProductDataFromDB();
                saveProductsToPhoneMemory();
                textResult.setText("response: " + response.code());
                notEditedProductsInDB.remove(editedProduct);
                saveNotEditedProductsToPhoneMemory();
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                textResult.setText("Failure update Pr!");
            }
        });
    }

    public void updateEditedProductIfExist() {
        Product editedProductR = adapterProduct.getEditedProduct();
        if (editedProductR != null) {
            int indexEditedPr = findProductIndexInList(productListFromDB, editedProductR);
            if (indexEditedPr >= 0) {
                productListFromDB.set(indexEditedPr, editedProductR);
                saveProductsToPhoneMemory();
            }
            int indexForEditedList = findProductIndexInList(notEditedProductsInDB, editedProductR);
            if (indexForEditedList < 0) {
                notEditedProductsInDB.add(editedProductR);
                saveNotEditedProductsToPhoneMemory();
            }else{
                notEditedProductsInDB.set(indexForEditedList, editedProductR);
                saveNotEditedProductsToPhoneMemory();
            }
            adapterProduct.ResetEditedProductForNull();
        }
    }

    public void DBupdateScannerEditedProduct() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    updateEditedProductIfExist();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void deleteProductFromDB() {
        ProductAPI productApi = retrofit.create(ProductAPI.class);
        Call<Void> call = productApi.deleteProduct(deletedProduct.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Not deleted from DB");
                    if (!notDeletedProductsInDB.contains(deletedProduct)) {
                        notDeletedProductsInDB.add(deletedProduct);
                        saveNotDeletedProductsToPhoneMemory();
                    }
                } else {
                    textResult.setText("deleted from DB");

                    if (notDeletedProductsInDB != null) {
                        try {
                            notDeletedProductsInDB.remove(deletedProduct);
                            saveNotDeletedProductsToPhoneMemory();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                textResult.setText("FAILuRE DELETED PR!");
            }
        });

    }

    public void removeDeletedProductIfExist() {
        Product deletedProductR = adapterProduct.getDeletedProduct();
        if (deletedProductR != null) {
            int indexNotDEl = findProductIndexInList(notDeletedProductsInDB, deletedProductR);
            if (indexNotDEl < 0) {
                notDeletedProductsInDB.add(deletedProductR);
            }
            saveNotDeletedProductsToPhoneMemory();
            int indexDEL = findProductIndexInList(productListFromDB, deletedProductR);
            if (indexDEL >= 0) {
                productListFromDB.remove(indexDEL);
            }
            saveProductsToPhoneMemory();
            int indexInNonUpdated = findProductIndexInList(notEditedProductsInDB, deletedProduct);
            if(indexInNonUpdated >= 0){
                notEditedProductsInDB.remove(indexInNonUpdated);
            }
            adapterProduct.ResetDeletedProductForNull();
        }
    }

    public void DBupdateScannerDeletedProduct() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    removeDeletedProductIfExist();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    void getShopDataFromDB() {
        ShopApi shopAPI = retrofit.create(ShopApi.class);
        Call<List<Shop>> call = shopAPI.getShops();
        call.enqueue(new Callback<List<Shop>>() {
            @Override
            public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Cant load shopsList");
                    return;
                }
                List<Shop> list = response.body();
                shopListFromDB.clear();
                shopListFromDB.addAll(list);
                saveShopsToPhoneMemory();
                textResult.setText("Loaded shops from DB");
            }

            @Override
            public void onFailure(Call<List<Shop>> call, Throwable t) {
                textResult.setText(t.getMessage());
            }
        });
    }

    private void addShopToDB() {
        ShopApi shopApi = retrofit.create(ShopApi.class);
        Call<Shop> call = shopApi.createPost(addedShop);
        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Code" + response.code());
                }
                Shop responseShop = response.body();
                textResult.setText("response: " + response.code());
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {
                textResult.setText(t.getMessage());
            }
        });
    }

    public void updateShopInDB() {
        ShopApi shopApi = retrofit.create(ShopApi.class);
        Call<Shop> call = shopApi.createPUT(editedShop.getId(), editedShop);
        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Code" + response.code());
                    if (notEditedShopsInDB.contains(editedShop)) {
                        notEditedShopsInDB.add(editedShop);
                        saveNotEditedShopsToPhoneMemory();
                    }
                }
                Shop responseShop = response.body();
                textResult.setText("response: " + response.code() + responseShop.getName());
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {
                textResult.setText(t.getMessage());
            }
        });
    }

    public void updateEditedShopIfExist() {
        Shop editedShopR = adapterShop.getEditedShop();
        if (editedShopR != null) {
            editedShop = editedShopR;
            updateShopInDB();
            getShopDataFromDB();
            saveShopsToPhoneMemory();
            adapterShop.resetEditedShopForNull();
        }
    }

    public void DBupdateScannerEditedShop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    updateEditedShopIfExist();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void deleteShopFromDB() {
        ShopApi shopApi = retrofit.create(ShopApi.class);
        Call<Void> call = shopApi.deleteShop(deletedShop.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    textResult.setText("Not deteled from DB! ");
                    if (notDeletedShopsInDB.contains(deletedShop)) {
                        notDeletedShopsInDB.add(deletedShop);
                        saveNotDeletedShopsToPhoneMemory();
                    }
                } else {
                    textResult.setText("deleted shop from DB");
                    if (notDeletedShopsInDB != null) {
                        try {
                            notDeletedShopsInDB.remove(deletedShop);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                textResult.setText(t.getMessage());
            }
        });
    }

    public void removeDeletedShopIfExist() {
        Shop deletedShopR = adapterShop.getDeletedShop();
        if (deletedShopR != null) {
            deletedShop = deletedShopR;
            deleteShopFromDB();
            shopListFromDB.remove(deletedShop);
            saveShopsToPhoneMemory();
            textResult.setText("DELETED!");
            adapterShop.resetDeletedShopForNull();
        }
    }

    public void DBupdateScannerDeletedShop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    removeDeletedShopIfExist();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onLocationChanged(Location location) {
        actualLongitude = location.getLongitude();
        actualLatitude = location.getLatitude();
        String coordinatess = "long: " + String.valueOf(actualLongitude) + " lat: " + String.valueOf(actualLatitude);
       // coordinates.setText(coordinatess);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*public void makeToastForAlarm() {

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "Hey, you are near: " +
                                nearestShop.getName() + "\n" +
                                "you can by your " + productListFromDB.size() +
                                " overdue product(s)" + "\n" + "from Alarms here :)",
                        Toast.LENGTH_LONG).show();
            }
        });
    }*/

    public void createRecyclerView() {
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.INVISIBLE);
        adapterProduct = new AdapterProduct(this);
        recyclerView.setAdapter(adapterProduct);
        adapterShop = new AdapterShop(this);
    }

    public void saveProductsToPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(productListFromDB);
        editor.putString("productList", json);
        editor.apply();

    }

    public void loadProductsFromPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String jsonLoad = sPref.getString("productList", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        productListFromDB = gsonLoad.fromJson(jsonLoad, type);

        if (productListFromDB == null) {
            productListFromDB = new ArrayList<>();
            textResult.setText("Empty products");
        }
    }

    public void saveAlarmsToPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmListFromDB);
        editor.putString("alarmList", json);
        editor.apply();
    }

    public void loadAlarmsFromPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String jsonLoad = sPref.getString("alarmList", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        alarmListFromDB = gsonLoad.fromJson(jsonLoad, type);

        if (alarmListFromDB == null) {
            alarmListFromDB = new ArrayList<>();
            textResult.setText("Empty alarms");
        }
    }

    public void saveShopsToPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(shopListFromDB);
        editor.putString("shopList", json);
        editor.apply();
      //  Toast.makeText(MainActivity.this, "saved shops to Phone!",
        //        Toast.LENGTH_SHORT).show();
    }

    public void loadShopsFromPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String jsonLoad = sPref.getString("shopList", null);
        Type type = new TypeToken<ArrayList<Shop>>() {
        }.getType();
        shopListFromDB = gsonLoad.fromJson(jsonLoad, type);

        if (shopListFromDB == null) {
            shopListFromDB = new ArrayList<>();
           // Toast.makeText(MainActivity.this, "loaded null list",
             //       Toast.LENGTH_SHORT).show();
        } else {
          //  Toast.makeText(MainActivity.this, "loaded list shops correctly",
            //        Toast.LENGTH_SHORT).show();
        }
    }

    public void updateAllDataFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    getAlarmDataFromDB();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getProductDataFromDB();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getShopDataFromDB();
                }
        }).start();
    }

    public void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void saveNotDeletedProductsToPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notDeletedProductsInDB);
        editor.putString("productsNotDeletedFromDB", json);
        editor.apply();
    }

    public void loadNotDeletedProductsFromPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String jsonLoad = sPref.getString("productsNotDeletedFromDB", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        notDeletedProductsInDB = gsonLoad.fromJson(jsonLoad, type);
        if (notDeletedProductsInDB == null) {
            notDeletedProductsInDB = new ArrayList<>();
        }
        coordinates.setText("loaded non deleted products correctly");
    }

    public void saveNotEditedProductsToPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notEditedProductsInDB);
        editor.putString("productsNotEditedFromDB", json);
        editor.apply();

    }

    public void loadNotEditedProductsFromPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String jsonLoad = sPref.getString("productsNotEditedFromDB", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        notEditedProductsInDB = gsonLoad.fromJson(jsonLoad, type);
        if (notEditedProductsInDB == null) {
            notEditedProductsInDB = new ArrayList<>();
        }
        coordinates.setText("Loaded non Edited list products correctly ");
    }

    public void saveNotDeletedShopsToPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notDeletedShopsInDB);
        editor.putString("shopNotDeletedFromDB", json);
        editor.apply();
       // Toast.makeText(MainActivity.this, "saved  non deleted shops to Phone!",
         //       Toast.LENGTH_SHORT).show();
    }

    public void loadNotDeletedShopsFromPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String jsonLoad = sPref.getString("shopNotDeletedFromDB", null);
        Type type = new TypeToken<ArrayList<Shop>>() {
        }.getType();
        notDeletedShopsInDB = gsonLoad.fromJson(jsonLoad, type);

        if (notDeletedShopsInDB == null) {
            notDeletedShopsInDB = new ArrayList<>();
          //  Toast.makeText(MainActivity.this, "loaded null list",
            //        Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(MainActivity.this, "loaded list notDELETED shops correctly",
          //      Toast.LENGTH_SHORT).show();
    }

    public void saveNotEditedShopsToPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notEditedShopsInDB);
        editor.putString("shopNotEditedFromDB", json);
        editor.apply();
      //  Toast.makeText(MainActivity.this, "saved  non edited shops to Phone!",
        //        Toast.LENGTH_SHORT).show();
    }

    public void loadNotEditedShopsFromPhoneMemory() {
        sPref = getPreferences(MODE_PRIVATE);
        Gson gsonLoad = new Gson();
        String jsonLoad = sPref.getString("shopNotEditedFromDB", null);
        Type type = new TypeToken<ArrayList<Shop>>() {
        }.getType();
        notEditedShopsInDB = gsonLoad.fromJson(jsonLoad, type);

        if (notEditedShopsInDB == null) {
            notEditedShopsInDB = new ArrayList<>();
          //  Toast.makeText(MainActivity.this, "loaded null list",
            //        Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(MainActivity.this, "loaded list notEDITED shop correctly",
          //      Toast.LENGTH_SHORT).show();
    }

    public void scannerNotActualisedDataToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    coordinates.setText(notDeletedProductsInDB.toString());
                    if (notDeletedProductsInDB != null) {
                        for (int i = 0; i < notDeletedProductsInDB.size(); i++) {
                            deletedProduct = notDeletedProductsInDB.get(i);
                            deleteProductFromDB();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (notDeletedShopsInDB != null) {
                        for (Shop s : notDeletedShopsInDB) {
                            deletedShop = s;
                            deleteShopFromDB();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (notEditedShopsInDB != null) {
                        for (Shop s : notEditedShopsInDB) {
                            editedShop = s;
                            updateShopInDB();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listsView.setText(notEditedProductsInDB.toString());
                    if (notEditedProductsInDB != null) {
                        for (Product p : notEditedProductsInDB) {
                            editedProduct = p;
                            upDateProductInDB();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public int findProductIndexInList(List<Product> productsFromDB, Product deletedProduct) {
        int index = -1;
        for (int i = 0; i < productsFromDB.size(); i++) {
            int answer = productsFromDB.get(i).getId().compareTo(deletedProduct.getId());
            if (answer != 1 && answer != -1) {
                index = i;
            }
        }
        return index;
    }
}