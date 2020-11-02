package com.company.blumeserver.ui.flower_list;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.blumeserver.Adapter.MyFlowerListAdapter;
import com.company.blumeserver.AddonEditActivity;
import com.company.blumeserver.Common.Common;
import com.company.blumeserver.Common.MySwipeHelper;
import com.company.blumeserver.EventBus.AddonEditEvent;
import com.company.blumeserver.EventBus.ChangeMenuClick;
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

public class FlowerListFragment extends Fragment {

    //Image upload
    private static final int PICK_IMAGE_REQUEST=1234;
    private ImageView img_flower;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private android.app.AlertDialog dialog;

    private FlowerListViewModel flowerListViewModel;
    private List<FlowerModel> flowerModelList;

    Unbinder unbinder;
    @BindView(R.id.recycler_flower_list)
    RecyclerView recycler_flower_list;

    LayoutAnimationController layoutAnimationController;
    MyFlowerListAdapter adapter ;
    private Uri imageUri=null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        flowerListViewModel =
                ViewModelProviders.of(this).get(FlowerListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_flower_list, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();
        flowerListViewModel.getMutableLiveDataFlowerList().observe(this, flowerModels -> {
            if (flowerModels !=null) {

                flowerModelList = flowerModels;
                adapter = new MyFlowerListAdapter(getContext(), flowerModelList);
                recycler_flower_list.setAdapter(adapter);
                recycler_flower_list.setLayoutAnimation(layoutAnimationController);
            }
        });
        return root;
    }

    private void initViews() {

        setHasOptionsMenu(true); //Enable menu in fragment

        dialog=new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.CategorySelected.getName());
        recycler_flower_list.setHasFixedSize(true);
        recycler_flower_list.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);

        //Get Size
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels;

        MySwipeHelper mySwipeHelper=new MySwipeHelper(getContext(),recycler_flower_list,width/6){
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),"Delete",30,0, Color.parseColor("#9b0000"),
                        pos -> {
                            if(flowerModelList!=null)
                                Common.selectedFlower=flowerModelList.get(pos);
                            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                            builder.setTitle("DELETE")
                                    .setMessage("Do you want to delete this flower?")
                                    .setNegativeButton("CANCEL",((dialogaInterface,i)->dialogaInterface.dismiss()))
                                    .setPositiveButton("DELETE",((dialogInterface,i)->{
                                        FlowerModel flowerModel=adapter.getItemAtPosition(pos); //Get item in adapter
                                        if(flowerModel.getPositionInList()==-1) //Default, do nothing
                                            Common.CategorySelected.getFlowers().remove(pos);
                                        else
                                            Common.CategorySelected.getFlowers().remove(flowerModel.getPositionInList()); //Remove by index we save
                                        updateFlower(Common.CategorySelected.getFlowers(),Common.ACTION.DELETE);

                                    }));
                            AlertDialog deleteDialog=builder.create();
                            deleteDialog.show();
                        }));

                buf.add(new MyButton(getContext(),"Update",30,0, Color.parseColor("#560027"),
                        pos -> {
                            FlowerModel flowerModel=adapter.getItemAtPosition(pos);
                            if(flowerModel.getPositionInList()==-1)
                                showUpdateDialog(pos,flowerModel);
                            else
                                showUpdateDialog(flowerModel.getPositionInList(),flowerModel);
                        }));
                buf.add(new MySwipeHelper.MyButton(getContext(),"Addon",30,0, Color.parseColor("#336699"),
                        pos -> {
                            FlowerModel flowerModel=adapter.getItemAtPosition(pos);
                            if(flowerModel.getPositionInList()==-1)
                                Common.selectedFlower=flowerModelList.get(pos);
                            else
                                Common.selectedFlower=flowerModel;
                            startActivity(new Intent(getContext(), AddonEditActivity.class));
                            if(flowerModel.getPositionInList()==-1)
                                EventBus.getDefault().postSticky(new AddonEditEvent(true,pos));
                            else
                                EventBus.getDefault().postSticky(new AddonEditEvent(true,flowerModel.getPositionInList()));
                        }));
            }
        };
        setHasOptionsMenu(true);

    }

    private void showAddDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("CREATE");
        builder.setMessage("Please fill information");

        View itemView=LayoutInflater.from(getContext()).inflate(R.layout.layout_update_flower,null);
        EditText edt_flower_name=(EditText)itemView.findViewById(R.id.edt_flower_name);
        EditText edt_flower_price=(EditText)itemView.findViewById(R.id.edt_flower_price);
        EditText edt_flower_description=(EditText)itemView.findViewById(R.id.edt_flower_description);
        img_flower=(ImageView)itemView.findViewById(R.id.img_flower_image);

        //Set data
        Glide.with(getContext()).load(R.drawable.ic_image_24).into(img_flower);

        //Set event
        img_flower.setOnClickListener(view->{
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL",((dialogInterface,i)->dialogInterface.dismiss()))
                .setPositiveButton("CREATE",((dialogInterface,i)->{
                    FlowerModel updateFlower=new FlowerModel();
                    updateFlower.setName(edt_flower_name.getText().toString());
                    updateFlower.setDescription(edt_flower_description.getText().toString());
                    updateFlower.setPrice(TextUtils.isEmpty(edt_flower_price.getText()) ? 0 :
                            Long.parseLong(edt_flower_price.getText().toString()));

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
                                updateFlower.setImage(uri.toString());
                                if(Common.CategorySelected.getFlowers()==null)
                                    Common.CategorySelected.setFlowers(new ArrayList<>());
                                Common.CategorySelected.getFlowers().add(updateFlower);
                                updateFlower(Common.CategorySelected.getFlowers(),Common.ACTION.CREATE);
                            });
                        }).addOnProgressListener(taskSnapshot -> {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                        });
                    }
                    else {
                        if(Common.CategorySelected.getFlowers()==null)
                            Common.CategorySelected.setFlowers(new ArrayList<>());
                        Common.CategorySelected.getFlowers().add(updateFlower);
                        updateFlower(Common.CategorySelected.getFlowers(),Common.ACTION.CREATE);
                    }
                }));

        builder.setView(itemView);
        AlertDialog updateDialog=builder.create();
        updateDialog.show();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.flower_list_menu,menu);

        MenuItem menuItem=menu.findItem(R.id.action_search);

        SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearchFlower(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //Clear text when click to Clear button on Search View
        ImageView closeButton=(ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view->{
            EditText ed=(EditText)searchView.findViewById(R.id.search_src_text);
            //Clear text
            ed.setText("");
            //Clear query
            searchView.setQuery("",false);
            //Collapse the action view
            searchView.onActionViewCollapsed();
            //Collapse the search widget
            menuItem.collapseActionView();
            //Restore result to original
            flowerListViewModel.getMutableLiveDataFlowerList().setValue(Common.CategorySelected.getFlowers());
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            showAddDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSearchFlower(String s) {
        List<FlowerModel> resultFlower=new ArrayList<>();
        for(int i=0;i<Common.CategorySelected.getFlowers().size();i++) {
            FlowerModel flowerModel=Common.CategorySelected.getFlowers().get(i);
            if (flowerModel.getName().toLowerCase().contains(s.toLowerCase())) {
                flowerModel.setPositionInList(i); //Save index
                resultFlower.add(flowerModel);
            }
        }
        flowerListViewModel.getMutableLiveDataFlowerList().setValue(resultFlower); //Set search result

    }

    private void showUpdateDialog(int pos,FlowerModel flowerModel) {

        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill information");

        View itemView=LayoutInflater.from(getContext()).inflate(R.layout.layout_update_flower,null);
        EditText edt_flower_name=(EditText)itemView.findViewById(R.id.edt_flower_name);
        EditText edt_flower_price=(EditText)itemView.findViewById(R.id.edt_flower_price);
        EditText edt_flower_description=(EditText)itemView.findViewById(R.id.edt_flower_description);
        img_flower=(ImageView)itemView.findViewById(R.id.img_flower_image);

        //Set data
        edt_flower_name.setText(new StringBuilder("").append(flowerModel.getName()));
        edt_flower_price.setText(new StringBuilder("").append(flowerModel.getPrice()));
        edt_flower_description.setText(new StringBuilder("").append(flowerModel.getDescription()));
        Glide.with(getContext()).load(flowerModel.getImage()).into(img_flower);

        //Set event
        img_flower.setOnClickListener(view->{
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        });
        builder.setNegativeButton("CANCEL",((dialogInteface,i)->dialogInteface.dismiss()))
                .setPositiveButton("UPDATE",((dialogInteface,i)->{
                    FlowerModel updateFlower=flowerModel;
                    updateFlower.setName(edt_flower_name.getText().toString());
                    updateFlower.setDescription(edt_flower_description.getText().toString());
                    updateFlower.setPrice(TextUtils.isEmpty(edt_flower_price.getText()) ? 0 :
                            Long.parseLong(edt_flower_price.getText().toString()));

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
                                updateFlower.setImage(uri.toString());
                                Common.CategorySelected.getFlowers().set(pos,updateFlower);
                                updateFlower(Common.CategorySelected.getFlowers(),Common.ACTION.UPDATE);
                            });
                        }).addOnProgressListener(taskSnapshot -> {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                        });
                    }
                    else {
                        Common.CategorySelected.getFlowers().set(pos,updateFlower);
                        updateFlower(Common.CategorySelected.getFlowers(),Common.ACTION.UPDATE);
                    }
                }));

        builder.setView(itemView);
        AlertDialog updateDialog=builder.create();
        updateDialog.show();
    }

    private void updateFlower(List<FlowerModel> flowers,Common.ACTION action) {
        Map<String,Object> updateData=new HashMap<>();
        updateData.put("flowers",flowers);

        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.CategorySelected.getMenu_id())
                .updateChildren(updateData)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                flowerListViewModel.getMutableLiveDataFlowerList();
                EventBus.getDefault().postSticky(new ToastEvent(action,true));
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            if(data!=null && data.getData()!=null){
                imageUri=data.getData();
                img_flower.setImageURI(imageUri);
            }
        }
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new ChangeMenuClick(true));
        super.onDestroy();
    }


}