package com.iadvigun.study;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ProductListHolder> {

    private Context parent;
    private List<Product> list = new ArrayList<>();
    Product editedProduct;
    Product deletedProduct;
    int alarmOrListPr = 0;


    public Product getDeletedProduct(){
        return deletedProduct;
    }
    public void ResetDeletedProductForNull(){
        deletedProduct = null;
    }

    public Product getEditedProduct(){
        return editedProduct;
    }

    public void ResetEditedProductForNull(){
        editedProduct = null;
    }

    public void setAlarmOrListPr(int alarmOrListPr){
        this.alarmOrListPr = alarmOrListPr;
    }



    public void setItems(List<Product> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void upDateListAdapter(){
        notifyDataSetChanged();
    }

    public void clearItems() {
        list.clear();
        notifyDataSetChanged();
    }
    public List<Product> getListFromAdapter (){
        return this.list;
    }



    public AdapterProduct(Context parent) {

        this.parent = parent;

    }


    @NonNull
    @Override
    public ProductListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int LayoutIdFroListItem = R.layout.product_layout;  // xml file source

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(LayoutIdFroListItem, parent, false);

        ProductListHolder viewHolder = new ProductListHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ProductListHolder holder, int position) {// update list
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductListHolder extends RecyclerView.ViewHolder {
        TextView productNameView;
        TextView productAmountView;
        TextView productExpirationView;
        TextView productOverdue;
        TextView buttonEditView;
        Button buttonDelete;
        Button buttonDone;
        ImageView imageDone;


        public ProductListHolder(@NonNull final View itemView) {
            super(itemView);
            productNameView = itemView.findViewById(R.id.tv_product_name);  // конверт в java object
            productAmountView = itemView.findViewById(R.id.tv_product_amount);
            productExpirationView = itemView.findViewById(R.id.tv_product_expiration);
            productOverdue = itemView.findViewById(R.id.tv_product_overdue);
            buttonEditView = itemView.findViewById(R.id.tv_button_edit);
            buttonDelete = itemView.findViewById(R.id.tv_button_delete);
            buttonDone = itemView.findViewById(R.id.tv_button_DONE);
            imageDone = itemView.findViewById(R.id.imageViewDone);

            if(alarmOrListPr == 1){
                buttonDone.setVisibility(View.VISIBLE);
                imageDone.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.INVISIBLE);
                productOverdue.setBackgroundResource((R.color.overDue_Red));
            }else if(alarmOrListPr == 2){
                buttonDone.setVisibility(View.INVISIBLE);
                imageDone.setVisibility(View.INVISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
                productOverdue.setBackgroundResource((R.color.overDue_noOne));
            }

            buttonDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(parent, "Done!" + getAdapterPosition(),
                            Toast.LENGTH_SHORT).show();
                   String name =  list.get(getAdapterPosition()).getName();
                   int amount = list.get(getAdapterPosition()).getAmount();
                   int expiration = list.get(getAdapterPosition()).getExpiration();

                    editedProduct = new Product(name, amount,
                            expiration);
                    editedProduct.setId((Long)list.get(getAdapterPosition()).getId());
                    list.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });


            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(parent, "Delete method" + getAdapterPosition(),
                            Toast.LENGTH_SHORT).show();
                    String Name = list.get(getAdapterPosition()).getName();
                    int Amount = list.get(getAdapterPosition()).getAmount();
                    int Expiration = list.get(getAdapterPosition()).getExpiration();

                    deletedProduct = new Product(Name, Amount,
                            Expiration);
                    deletedProduct.setId((Long)list.get(getAdapterPosition()).getId());

                  //  deletedProduct = list.get(getAdapterPosition());    ?????????
                    list.remove(list.get(getAdapterPosition()));
                    notifyDataSetChanged();

                }
            });


            buttonEditView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(parent, "You clicked on button edit",
                            Toast.LENGTH_SHORT).show();

                    final Dialog dialog = new Dialog(parent);
                    dialog.setContentView(R.layout.dialog_product_layout);
                    dialog.setTitle("Product");

                    final EditText editTextName = (EditText) dialog.findViewById(R.id.edit_name);
                    editTextName.setText(list.get(getAdapterPosition()).getName());

                    final EditText editTextAmount =(EditText) dialog.findViewById(R.id.edit_amount);
                    editTextAmount.setText(String.valueOf(list.get(getAdapterPosition()).getAmount()));

                    final EditText editTextExpiration =(EditText) dialog.findViewById(R.id.edit_expiration);
                    editTextExpiration.setText(String.valueOf(list.get(getAdapterPosition()).getExpiration()));

                    Button dialogButtonAccept = (Button) dialog.findViewById(R.id.button_accept);
                    Button dialogButtonCancel = (Button) dialog.findViewById(R.id.button_cancel);
                    Button dialogButtonPlusAmount = (Button) dialog.findViewById((R.id.button_plus_Amount));
                    Button dialogButtonPlusExpiration = (Button) dialog.findViewById((R.id.button_plus_Expiration));
                    Button dialogButtonMinusAmount = (Button) dialog.findViewById((R.id.button_minus_Amount));
                    Button dialogButtonMinusExpiration = (Button) dialog.findViewById((R.id.button_minus_Expiration));
                    Button dialogClose = (Button) dialog.findViewById(R.id.button_close);

                    dialog.show();

                    dialogButtonPlusAmount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String inputedAmount = editTextAmount.getText().toString();
                            if(inputedAmount.equals("")){inputedAmount = "0"; }
                            int inputedAm = Integer.parseInt(inputedAmount);
                            if(inputedAm < 0){inputedAm = 0;}
                            inputedAm ++;
                            editTextAmount.setText(String.valueOf(inputedAm));

                        }
                    });

                    dialogButtonPlusExpiration.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String inputedExpiration = editTextExpiration.getText().toString();
                            if(inputedExpiration.equals("")){inputedExpiration = "0"; }
                            int inputedExp = Integer.parseInt(inputedExpiration);
                            if(inputedExp < 0){inputedExp = 0;}
                            inputedExp ++;
                            editTextExpiration.setText(String.valueOf(inputedExp));
                        }
                    });

                    dialogButtonMinusAmount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String inputedAmount = editTextAmount.getText().toString();
                            if(inputedAmount.equals("")){inputedAmount = "0"; }
                            int inputedAm = Integer.parseInt(inputedAmount);
                            if(inputedAm <= 0){inputedAm = 0;}else{
                                inputedAm --;}
                            editTextAmount.setText(String.valueOf(inputedAm));

                        }
                    });

                    dialogButtonMinusExpiration.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String inputedExpiration = editTextExpiration.getText().toString();
                            if(inputedExpiration.equals("")){inputedExpiration = "0"; }
                            int inputedExp = Integer.parseInt(inputedExpiration);
                            if(inputedExp <= 0){inputedExp = 0;}else{
                                inputedExp --;}
                            editTextExpiration.setText(String.valueOf(inputedExp));
                        }
                    });

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
                           String inputedAmount = editTextAmount.getText().toString();
                            if(inputedAmount.equals("")){inputedAmount = "0"; }
                           list.get(getAdapterPosition()).setAmount(Integer.parseInt(inputedAmount));
                           String inputedExpiration = editTextExpiration.getText().toString();
                            if (inputedExpiration.equals("")){inputedExpiration = "0";}
                           list.get(getAdapterPosition()).setExpiration(Integer.parseInt(inputedExpiration));


                            editedProduct = new Product(inputedName, Integer.parseInt(inputedAmount),
                                    Integer.parseInt(inputedExpiration));
                            editedProduct.setId((Long)list.get(getAdapterPosition()).getId());

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

       // }
         //   });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Toast.makeText(parent, "You clicked on toast" + position,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        void bind(Product product) {
           // String pattern = "yyyy-MM-dd";
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            productNameView.setText(product.getName());
            productAmountView.setText("amount:     " + product.getAmount());
            productExpirationView.setText("expiration:  " + product.getExpiration() + "  day(s)");
            productOverdue.setText("overdue:  " + product.getOverdueDate());


        }
    }

}
