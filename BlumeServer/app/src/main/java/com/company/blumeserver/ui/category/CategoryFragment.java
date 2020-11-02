package com.company.blumeserver.ui.category;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.blumeserver.Adapter.MyCategoriesAdapter;
import com.company.blumeserver.Common.Common;
import com.company.blumeserver.Common.MySwipeHelper;
import com.company.blumeserver.Common.SpacesItemDecoration;
import com.company.blumeserver.EventBus.ToastEvent;
import com.company.blumeserver.Model.CategoryModel;
import com.company.blumeserver.Model.FlowerModel;
import com.company.blumeserver.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class CategoryFragment extends Fragment {


    private static final int PICK_IMAGE_REQUEST =1234 ;
    private CategoryViewModel categoryViewModel;
    private Uri imageUri=null;
    List<CategoryModel> categoryModels;
    FirebaseStorage storage;
    StorageReference storageReference;

    Unbinder unbinder;
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    android.app.AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyCategoriesAdapter adapter;
    ImageView img_category;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoryViewModel =
                ViewModelProviders.of(this).get(CategoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();
        categoryViewModel.getMessageError().observe(this,s -> {
            Toast.makeText(getContext(),""+s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        categoryViewModel.getCategoryListMutable().observe(this, categoryModelList -> {
            dialog.dismiss();
            categoryModels=categoryModelList;
            adapter = new MyCategoriesAdapter(getContext(),categoryModels);
            recycler_menu.setAdapter(adapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);
        });

        return root;
    }

    private void initViews() {


        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        //dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper=new MySwipeHelper(getContext(),recycler_menu,200){
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),"Delete",30,0, Color.parseColor("#333639"),
                        pos -> {
                            Common.CategorySelected=categoryModels.get(pos);
                            showDeleteDialog();

                        }));
                buf.add(new MyButton(getContext(),"Update",30,0, Color.parseColor("#560027"),
                        pos -> {
                            Common.CategorySelected=categoryModels.get(pos);
                            showUpdateDialog();

                        }));
            }
        };
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_create){
            showAddDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Create");
        builder.setMessage("Please fill information");

        View itemView=LayoutInflater.from(getContext()).inflate(R.layout.layout_update_category,null);
        EditText edt_category_name=(EditText)itemView.findViewById(R.id.edt_category_name);
        img_category=(ImageView)itemView.findViewById(R.id.img_category);

        //Set Data
        Glide.with(getContext()).load(R.drawable.ic_image_24).into(img_category);

        //Set Event
        img_category.setOnClickListener(view->{
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL",(dialogInteface,i)->dialogInteface.dismiss());
        builder.setPositiveButton("CREATE",(dialogInteface,i)->{

            CategoryModel categoryModel=new CategoryModel();
            categoryModel.setName(edt_category_name.getText().toString());
            categoryModel.setFlowers(new ArrayList<>());
            if(imageUri!=null)
            {
                //In this we will use Firebase Storage to upload
                dialog.setMessage("Uploading...");
                dialog.show();

                String unique_name= UUID.randomUUID().toString();
                StorageReference imageFolder=storageReference.child("image/"+unique_name);

                imageFolder.putFile(imageUri)
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                        categoryModel.setImage(uri.toString());
                        addCategory(categoryModel);
                    });
                }).addOnProgressListener(taskSnapshot -> {
                    double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                });
            }
            else{
                addCategory(categoryModel);
            }
        });

        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        dialog.show();
    }
    private void addCategory(CategoryModel categoryModel) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .push()
                .setValue(categoryModel)
                .addOnFailureListener(e-> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    categoryViewModel.loadCategories();
                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.CREATE,false));
                });

    }

    private void showDeleteDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Do you really want to delete this item?");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCategory();
            }
        });

        androidx.appcompat.app.AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void deleteCategory() {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.CategorySelected.getMenu_id())
                .removeValue()
                .addOnFailureListener(e-> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    categoryViewModel.loadCategories();
                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.DELETE,true));
                });
    }


    private void showUpdateDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill information");

        View itemView=LayoutInflater.from(getContext()).inflate(R.layout.layout_update_category,null);
        EditText edt_category_name=(EditText)itemView.findViewById(R.id.edt_category_name);
        img_category=(ImageView)itemView.findViewById(R.id.img_category);

        //Set Data
        edt_category_name.setText(new StringBuilder("").append(Common.CategorySelected.getName()));
        Glide.with(getContext()).load(Common.CategorySelected.getImage()).into(img_category);

        //Set Event
        img_category.setOnClickListener(view->{
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL",(dialogInteface,i)->dialogInteface.dismiss());
        builder.setPositiveButton("UPDATE",(dialogInteface,i)->{
            Map<String,Object> updateData=new HashMap<>();
            updateData.put("name",edt_category_name.getText().toString());
            if(imageUri!=null)
            {
                //In this we will use Firebase Storage to upload
                dialog.setMessage("Uploading...");
                dialog.show();

                String unique_name= UUID.randomUUID().toString();
                StorageReference imageFolder=storageReference.child("image/"+unique_name);

                imageFolder.putFile(imageUri)
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateData.put("image",uri.toString());
                        updateCategory(updateData);
                    });
                }).addOnProgressListener(taskSnapshot -> {
                    double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                });
            }
            else{
                updateCategory(updateData);
            }
        });

        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void updateCategory(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.CategorySelected.getMenu_id())
                .updateChildren(updateData)
                .addOnFailureListener(e-> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    categoryViewModel.loadCategories();
                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.UPDATE,false));
                });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            if(data!=null && data.getData()!=null){
                imageUri=data.getData();
                img_category.setImageURI(imageUri);
            }
        }
    }
}