package com.company.blumeSunzi.ui.flower_list;

import android.app.SearchManager;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.blumeSunzi.Adapter.MyFlowerListAdapter;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.EventBus.MenuItemBack;
import com.company.blumeSunzi.Model.FlowerModel;
import com.company.blumeSunzi.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.security.AccessController.getContext;

public class FlowerListFragment extends Fragment {

    private FlowerListViewModel flowerListViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_flower_list)
    RecyclerView recycler_flower_list;

    LayoutAnimationController layoutAnimationController;
    MyFlowerListAdapter adapter ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        flowerListViewModel =
                ViewModelProviders.of(this).get(FlowerListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_flower_list, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();
        flowerListViewModel.getMutableLiveDataFlowerList().observe(this, new Observer<List<FlowerModel>>()
        {
            @Override
            public void onChanged(List<FlowerModel> flowerModels){
                if(flowerModels!=null){
                    adapter = new MyFlowerListAdapter(getContext(),flowerModels);
                    recycler_flower_list.setAdapter(adapter);
                    recycler_flower_list.setLayoutAnimation(layoutAnimationController);
                }
            }
        });
        return root;
    }

    private void initViews() {

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.CategorySelected.getName());

        setHasOptionsMenu(true);

        recycler_flower_list.setHasFixedSize(true);
        recycler_flower_list.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem menuItem=menu.findItem(R.id.action_search);

        SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Clear text when click to clear button on Search View
        ImageView closeButton=(ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view->{
            EditText ed = (EditText)searchView.findViewById(R.id.search_src_text);
            //Clear text;
            ed.setText("");
            //Clear query
            searchView.setQuery("",false);
            //Collapse the action view
            searchView.onActionViewCollapsed();
            //Collapse the search widget
            menuItem.collapseActionView();
            //Restore result to original
            flowerListViewModel.getMutableLiveDataFlowerList();
        });
    }

    private void startSearch(String s){
        List<FlowerModel> resultList=new ArrayList<>();
        for(int i=0; i<Common.CategorySelected.getFlowers().size();i++){
            FlowerModel flowerModel=Common.CategorySelected.getFlowers().get(i);
            if(flowerModel.getName().toLowerCase().contains(s))
                resultList.add(flowerModel);

        }
        flowerListViewModel.getMutableLiveDataFlowerList().setValue(resultList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}

