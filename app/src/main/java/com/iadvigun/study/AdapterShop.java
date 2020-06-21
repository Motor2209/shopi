package com.iadvigun.study;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class AdapterShop extends RecyclerView.Adapter<AdapterShop.ShopListHolder> {
    private Context parent;
    private List<Shop> list = new ArrayList<>();
    Shop deletedShop = null;
    Shop editedShop = null;


    public  Shop getDeletedShop(){
        return deletedShop;
    }

    public void resetDeletedShopForNull(){
        deletedShop = null;
    }

    public  Shop getEditedShop(){
        return editedShop;
    }

    public void resetEditedShopForNull(){
        editedShop = null;
    }


    public void setItems(List<Shop> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void clearItems() {
        list.clear();
        notifyDataSetChanged();
    }


    public AdapterShop(Context parent) {

        this.parent = parent;

    }


    @NonNull
    @Override
    public AdapterShop.ShopListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int LayoutIdFroListItem = R.layout.shop_layout;  // xml file source   !!!!!!!

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(LayoutIdFroListItem, parent, false);

        AdapterShop.ShopListHolder viewHolder = new AdapterShop.ShopListHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShop.ShopListHolder holder, int position) {// update list
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ShopListHolder extends RecyclerView.ViewHolder {
        TextView shopNameView;
        TextView shopLatitudeView;
        TextView shopLongitudeView;
        TextView shopAreaSize;
        TextView buttonEditView;
        Button buttonDeleteShop;

        public ShopListHolder(@NonNull View itemView) {
            super(itemView);
            shopNameView = itemView.findViewById(R.id.tv_shop_name);  // конверт в java object
            shopLatitudeView = itemView.findViewById(R.id.tv_shop_latitude);
            shopLongitudeView = itemView.findViewById(R.id.tv_shop_longitude);
            shopAreaSize = itemView.findViewById(R.id.tv_shop_areaSize);
            buttonEditView = itemView.findViewById(R.id.tv_button_edit_shop);
            buttonDeleteShop = itemView.findViewById(R.id.tv_button_delete_shop);

            buttonDeleteShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(parent, "Delete method" + getAdapterPosition(),
                            Toast.LENGTH_SHORT).show();
                    String name = list.get(getAdapterPosition()).getName();
                    float  latitude = list.get(getAdapterPosition()).getLatitude();
                    float longitude = list.get(getAdapterPosition()).getLongitude();
                    int areaSize = list.get(getAdapterPosition()).getAreaSize();

                    deletedShop = new Shop(name, latitude,
                            longitude, areaSize);
                    deletedShop.setId((Long)list.get(getAdapterPosition()).getId());
                   // deletedShop = list.get(getAdapterPosition());   ?????????
                    list.remove(getAdapterPosition());
                    notifyDataSetChanged();

                }
            });

            buttonEditView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(parent, "You clicked on button edit",
                            Toast.LENGTH_SHORT).show();

                    final Dialog dialog = new Dialog(parent);
                    dialog.setContentView(R.layout.dialog_shop_layout);
                    dialog.setTitle("Shop");

                    final EditText editTextName = dialog.findViewById(R.id.edit_name_shop);
                    editTextName.setText(list.get(getAdapterPosition()).getName());

                    final EditText editTextLatitude = dialog.findViewById(R.id.edit_latitude);
                    editTextLatitude.setText(String.valueOf(list.get(getAdapterPosition()).getLatitude()));

                    final EditText editTextLongitude = dialog.findViewById(R.id.edit_longitude);
                    editTextLongitude.setText(String.valueOf(list.get(getAdapterPosition()).getLongitude()));

                    final EditText editTextAreaSize = dialog.findViewById(R.id.edit_area_Size);
                    editTextAreaSize.setText(String.valueOf(list.get(getAdapterPosition()).getAreaSize()));


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
                            Toast.makeText(parent, "Edition method" + getAdapterPosition(),
                                    Toast.LENGTH_SHORT).show();
                            String inputedName = editTextName.getText().toString();
                            list.get(getAdapterPosition()).setName(inputedName);
                            String inputedLatitude = editTextLatitude.getText().toString();
                            if(inputedLatitude.equals("")){inputedLatitude = "0"; }
                            list.get(getAdapterPosition()).setLatitude(Float.parseFloat(inputedLatitude));
                            String inputedLongitude = editTextLongitude.getText().toString();
                            if (inputedLongitude.equals("")){inputedLongitude = "0";}
                            list.get(getAdapterPosition()).setLongitude(Float.parseFloat(inputedLongitude));
                            String inputedAreaSize = editTextAreaSize.getText().toString();
                            if (inputedAreaSize.equals("")){inputedLongitude = "0";}
                            list.get(getAdapterPosition()).setAreaSize(Integer.parseInt(inputedAreaSize));

                            editedShop = new Shop(inputedName, Float.parseFloat(inputedLatitude),
                                    Float.parseFloat(inputedLongitude), Integer.parseInt(inputedAreaSize));
                            editedShop.setId((Long)list.get(getAdapterPosition()).getId());

                            notifyDataSetChanged();
                            dialog.dismiss();
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
        }

        void bind(Shop shop) {

            shopNameView.setText(shop.getName());
            shopLatitudeView.setText("latitude: " + String.valueOf(shop.getLatitude()));
            shopLongitudeView.setText("longitude: " + String.valueOf(shop.getLongitude()));
            shopAreaSize.setText("area size: " + shop.getAreaSize() + " м");

        }
    }
}

