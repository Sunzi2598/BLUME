package com.company.blumeserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.company.blumeserver.Adapter.MyAddonAdapter;
import com.company.blumeserver.Common.Common;
import com.company.blumeserver.EventBus.AddonEditEvent;
import com.company.blumeserver.EventBus.SelectAddonModel;
import com.company.blumeserver.EventBus.UpdateAddonModel;
import com.company.blumeserver.Model.AddonModel;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddonEditActivity extends AppCompatActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.edt_name)
    EditText edt_name;
    @BindView(R.id.edt_price)
    EditText edt_price;
    @BindView(R.id.btn_create)
    Button btn_create;
    @BindView(R.id.btn_edit)
    Button btn_edit;
    @BindView(R.id.recycler_addon)
    RecyclerView recycler_addon;

    //Variable
    MyAddonAdapter addonAdapter;
    private int flowerEditPosition=-1;
    private boolean needSave=false;
    private boolean isAddon=false;

    //Event
    @OnClick(R.id.btn_create)
    void onCreateNew(){
        if(isAddon){
            if(addonAdapter!=null){
                AddonModel addonModel=new AddonModel();
                addonModel.setName(edt_name.getText().toString());
                addonModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                addonAdapter.addNewAddon(addonModel);
            }
        }
    }
    @OnClick(R.id.btn_edit)
    void onEdit(){
        if(isAddon) {
            if (addonAdapter != null) {
                AddonModel addonModel = new AddonModel();
                addonModel.setName(edt_name.getText().toString());
                addonModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                addonAdapter.editAddon(addonModel);
            }
        }
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addon_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_save:
                saveData();
                break;
            case android.R.id.home:{
                if(needSave){
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("Cancel?")
                            .setMessage("Do you really want close without saving?")
                            .setNegativeButton("CANCEL", (dialogInterface, which) -> dialogInterface.dismiss())
                            .setPositiveButton("OK", (dialogInterface, which) -> {
                                needSave=false;
                                closeActivity();
                            });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
                else {
                    closeActivity();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        if(flowerEditPosition!=-1){
            Common.CategorySelected.getFlowers().set(flowerEditPosition,Common.selectedFlower); //Save flower to category

            Map<String,Object> updateData=new HashMap<>();
            updateData.put("flowers",Common.CategorySelected.getFlowers());

            FirebaseDatabase.getInstance()
                    .getReference(Common.CATEGORY_REF)
                    .child(Common.CategorySelected.getMenu_id())
                    .updateChildren(updateData)
                    .addOnFailureListener(e -> Toast.makeText(AddonEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(this, "Reload success!", Toast.LENGTH_SHORT).show();
                            needSave=false;
                            edt_name.setText("");
                            edt_price.setText("0");
                        }
                    });
        }
    }

    private void closeActivity() {
        edt_name.setText("");
        edt_price.setText(0);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addon_edit);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recycler_addon.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_addon.setLayoutManager(layoutManager);
        recycler_addon.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }

    //Register event

    @Override
    protected void onStart(){
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //Receive event
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onAddonReceive(AddonEditEvent event){
        if(event.isAddon()){
            if(Common.selectedFlower.getAddon()==null)
                Common.selectedFlower.setAddon(new ArrayList<>());
                addonAdapter=new MyAddonAdapter(this,Common.selectedFlower.getAddon());
                flowerEditPosition=event.getPos(); //Save flower edit to update
                recycler_addon.setAdapter(addonAdapter);
                isAddon=event.isAddon();

        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onAddonModelUpdate(UpdateAddonModel event){
        if(event.getAddonModels()!=null){
            needSave=true;
            Common.selectedFlower.setAddon(event.getAddonModels());
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onSelectAddonModel(SelectAddonModel event){
        if(event.getAddonModel()!=null){
            edt_name.setText(event.getAddonModel().getName());
            edt_price.setText(String.valueOf(event.getAddonModel().getPrice()));

            btn_edit.setEnabled(true);
        }
        else {
            btn_edit.setEnabled(false);
        }
    }
}
