package com.company.blumeSunzi.ui.flower_detail;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.company.blumeSunzi.Adapter.MyFlowerListAdapter;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Database.CartDataSource;
import com.company.blumeSunzi.Database.CartDatabase;
import com.company.blumeSunzi.Database.CartItem;
import com.company.blumeSunzi.Database.LocalCartDataSource;
import com.company.blumeSunzi.EventBus.CounterCartEvent;
import com.company.blumeSunzi.EventBus.MenuItemBack;
import com.company.blumeSunzi.Model.AddonModel;
import com.company.blumeSunzi.Model.CommentModel;
import com.company.blumeSunzi.Model.FlowerModel;
import com.company.blumeSunzi.R;
import com.company.blumeSunzi.ui.comments.CommentFragment;
import com.company.blumeSunzi.ui.flower_list.FlowerListViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FlowerDetailFragment extends Fragment implements TextWatcher{

    private FlowerDetailViewModel flowerDetailViewModel;
    private Unbinder unbinder;
    private android.app.AlertDialog waitingDialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BottomSheetDialog addonBottomSheetDialog;
    private CartDataSource cartDataSource;
    
    //View need inflate
    ChipGroup chip_group_addon;
    EditText edt_search;

    @BindView(R.id.img_flower)
    ImageView img_flower;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btn_rating;
    @BindView(R.id.flower_name)
    TextView flower_name;
    @BindView(R.id.flower_description)
    TextView flower_description;
    @BindView(R.id.flower_price)
    TextView flower_price;
    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnShowComment)
    Button btnShowComment;
    @BindView(R.id.img_add_addon)
    ImageView img_add_on;
    @BindView(R.id.chip_group_user_selected_addon)
    ChipGroup chip_group_user_selected_addon;

    @OnClick(R.id.btn_rating)
    void onRatingButtonClick() {
        showDialogRating();
    }

    @OnClick(R.id.btnShowComment)
    void onShowCommentButtonClick(){
        CommentFragment commentFragment=CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(),"CommentFragment");
    }

    @OnClick(R.id.img_add_addon)
    void onAddonClick(){
        if(Common.selectedFlower.getAddon() !=null)
        {
            displayAddonList(); // Show all addon options
            addonBottomSheetDialog.show();
        }
    }

    @OnClick(R.id.btnCart)
    void onCartItemAdd(){
        CartItem cartItem = new CartItem();
        cartItem.setUid(Common.currentUser.getUid());
        cartItem.setUserPhone(Common.currentUser.getPhone());
        cartItem.setCategoryId(Common.CategorySelected.getMenu_id());
        cartItem.setFlowerId(Common.selectedFlower.getId());
        cartItem.setFlowerName(Common.selectedFlower.getName());
        cartItem.setFlowerImage(Common.selectedFlower.getImage());
        cartItem.setFlowerPrice(Double.valueOf(String.valueOf(Common.selectedFlower.getPrice())));
        cartItem.setFlowerQuantity(Integer.valueOf(numberButton.getNumber()));
        cartItem.setFlowerExtraPrice(Common.calculateExtraPrice(Common.selectedFlower.getUserSelectedAddon()));

        if(Common.selectedFlower.getUserSelectedAddon() !=null)
            cartItem.setFlowerAddon(new Gson().toJson(Common.selectedFlower.getUserSelectedAddon()));
        else
            cartItem.setFlowerAddon("Default");

        cartDataSource.getItemWithAllOptionsInCart(Common.currentUser.getUid(),
                Common.CategorySelected.getMenu_id(),
                cartItem.getFlowerId(),
                cartItem.getFlowerAddon())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CartItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(CartItem cartItemFromDB) {
                        if(cartItemFromDB.equals(cartItem)){
                            //Already in database, just update
                            cartItemFromDB.setFlowerExtraPrice(cartItem.getFlowerExtraPrice());
                            cartItemFromDB.setFlowerAddon(cartItem.getFlowerAddon());
                            cartItemFromDB.setFlowerQuantity(cartItemFromDB.getFlowerQuantity()+cartItem.getFlowerQuantity());


                            cartDataSource.updateCartItems(cartItemFromDB)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            Toast.makeText(getContext(), "Update Cart succes", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), "[UPDATE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            // Item not available in cart before, insert new
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to Cart success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    },throwable -> {
                                        Toast.makeText(getContext(), "[CART ERROR]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e.getMessage().contains("empty")){
                            //Default, if Cart is empty, this code will be fired
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to Cart success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    },throwable -> {
                                        Toast.makeText(getContext(), "[CART ERROR]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        }
                        else
                            Toast.makeText(getContext(), "[GET CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private void showDialogRating() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Rating Flower");
        builder.setMessage("Please Fill Information");
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating, null);
        RatingBar ratingBar=(RatingBar)itemView.findViewById(R.id.ratingBar);
        EditText edt_comment=(EditText)itemView.findViewById(R.id.edt_comment);
        builder.setView(itemView);

        builder.setNegativeButton("CANCEL",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("OK",(dialogInterface, i) ->{
            CommentModel commentModel = new CommentModel();
            commentModel.setName(Common.currentUser.getName());
            commentModel.setUid(Common.currentUser.getUid());
            commentModel.setComment(edt_comment.getText().toString());
            commentModel.setRatingValue(ratingBar.getRating());
            Map<String,Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
            commentModel.setCommentTimeStamp(serverTimeStamp);

            flowerDetailViewModel.setCommentModel(commentModel);
        });
        AlertDialog dialog=builder.show();
        dialog.show();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        flowerDetailViewModel =
                ViewModelProviders.of(this).get(FlowerDetailViewModel.class);
        View root = inflater.inflate(R.layout.fragment_flower_detail, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();
        flowerDetailViewModel.getMutableLiveDataFlower().observe(this, FlowerModel ->{
            displayInfo(FlowerModel);
        });
        flowerDetailViewModel.getMutableLiveDataComment().observe(this,CommentModel -> {
            submitRatingToFirebase(CommentModel);
        });
        return root;

    }

    private void initViews() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        addonBottomSheetDialog=new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        chip_group_addon=(ChipGroup)layout_addon_display.findViewById(R.id.chip_group_addon);
        edt_search=(EditText)layout_addon_display.findViewById(R.id.edt_search);
        addonBottomSheetDialog.setContentView(layout_addon_display);

        addonBottomSheetDialog.setOnDismissListener(dialogInterface ->{
            displayUserSelectedAddon();
            calculateTotalPrice();
        });

    }

    private void displayAddonList() {
        if(Common.selectedFlower.getAddon().size()>0){
            chip_group_addon.clearCheck(); // Clear check views
            chip_group_addon.removeAllViews();

            edt_search.addTextChangedListener(this);

            //Add all view

            for(AddonModel addonModel:Common.selectedFlower.getAddon()){

                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton,b)-> {
                    if(b){
                        if(Common.selectedFlower.getUserSelectedAddon()==null)
                            Common.selectedFlower.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFlower.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);
            }

        }
    }
    private void displayUserSelectedAddon() {
        if(Common.selectedFlower.getUserSelectedAddon() !=null &&
                Common.selectedFlower.getUserSelectedAddon().size()>0){
            chip_group_user_selected_addon.removeAllViews(); // Clear all view already added
            for(AddonModel addonModel : Common.selectedFlower.getUserSelectedAddon()) // Add all available addon to list
            {
                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(view ->{
                    // Remove whe user select delete
                    chip_group_user_selected_addon.removeView(view);
                    Common.selectedFlower.getUserSelectedAddon().remove(addonModel);
                    calculateTotalPrice();
                });
                chip_group_user_selected_addon.addView(chip);
            }
        }else if(Common.selectedFlower.getUserSelectedAddon().size()==0)
        {
            chip_group_user_selected_addon.removeAllViews();
        }
    }


    private void submitRatingToFirebase(CommentModel commentModel) {
        waitingDialog.show();
        //First, we will submit to Comments Ref
        FirebaseDatabase.getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.selectedFlower.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        //After submit to Comment Ref, we will update in Flower
                        addRatingToFlower(commentModel.getRatingValue());
                    }
                    waitingDialog.dismiss();
                });
    }

    private void addRatingToFlower(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.CategorySelected.getMenu_id()) //Select category
                .child("flowers") //Select array 'flowers' of this category
                .child(Common.selectedFlower.getKey()) //Because flower item is array list so key is index of arraylist
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            FlowerModel flowerModel = dataSnapshot.getValue(FlowerModel.class);
                            flowerModel.setKey(Common.selectedFlower.getKey()); //Don't forget set it

                            //Apply rating
                            if(flowerModel.getRatingValue()==null)
                                flowerModel.setRatingValue(0d); // d = D lower case
                            if(flowerModel.getRatingCount()==null)
                                flowerModel.setRatingCount(0l); // l = L lower case
                            double sumRating = flowerModel.getRatingValue()+ratingValue;
                            long ratingCount = flowerModel.getRatingCount()+1;


                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("ratingValue",sumRating);
                            updateData.put("ratingCount",ratingCount);

                            //Update data
                            flowerModel.setRatingValue(sumRating);
                            flowerModel.setRatingCount(ratingCount);

                            dataSnapshot.getRef()
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(task ->{
                                        waitingDialog.dismiss();
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Thank you!", Toast.LENGTH_SHORT).show();
                                            Common.selectedFlower = flowerModel;
                                            flowerDetailViewModel.setFlowerModel(flowerModel); // Call refresh
                                        }
                                    });
                        }
                        else
                            waitingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        waitingDialog.dismiss();
                        Toast.makeText(getContext(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void displayInfo(FlowerModel flowerModel) {
        Glide.with(getContext()).load(flowerModel.getImage()).into(img_flower);
        flower_name.setText(new StringBuilder(flowerModel.getName()));
        flower_description.setText(new StringBuilder(flowerModel.getDescription()));
        flower_price.setText(new StringBuilder(flowerModel.getPrice().toString()));

        if(flowerModel.getRatingValue() !=null)
            ratingBar.setRating(flowerModel.getRatingValue().floatValue()/flowerModel.getRatingCount());

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.selectedFlower.getName());

        calculateTotalPrice();
    }

    private void calculateTotalPrice(){
        double totalPrice = Double.parseDouble(Common.selectedFlower.getPrice().toString()),displayPrice=0.0;

        if(Common.selectedFlower.getUserSelectedAddon() !=null && Common.selectedFlower.getUserSelectedAddon().size()>0)
            for(AddonModel addOnModel : Common.selectedFlower.getUserSelectedAddon())
                totalPrice+=Double.parseDouble(addOnModel.getPrice().toString());

        displayPrice = totalPrice * (Integer.parseInt(numberButton.getNumber()));
        displayPrice = Math.round(displayPrice*100.0/100.0);

        flower_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        chip_group_addon.clearCheck();
        chip_group_addon.removeAllViews();

        for(AddonModel addonModel:Common.selectedFlower.getAddon()){
            if(addonModel.getName().toLowerCase().contains(s.toString().toLowerCase()))
            {
                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton,b)-> {
                    if(b){
                        if(Common.selectedFlower.getUserSelectedAddon()==null)
                            Common.selectedFlower.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFlower.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}

