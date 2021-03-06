package com.codingcrazz.notex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codingcrazz.notex.database.NoteEntity;
import com.codingcrazz.notex.model.NotesAdapter;
import com.codingcrazz.notex.viewmodels.ListActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private List<NoteEntity> mNotesList = new ArrayList<>();
    private ListActivityViewModel mViewModel;
    NotesAdapter mNotesAdapter;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.notes_recyclerview)
    RecyclerView mRecyclerView;

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.fab_add_note)
    void OnFabClicked() {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewModel();
        ButterKnife.bind(this);
        initRecyclerView();

//        mNotesList = mViewModel.mNotesList;
//        showData();
    }

    private void initViewModel() {

        Observer<List<NoteEntity>> notesObserver = new Observer<List<NoteEntity>>() {

            @Override
            public void onChanged(List<NoteEntity> noteEntities) {
                mNotesList.clear();
                mNotesList.addAll(noteEntities);
                if (mNotesAdapter == null) {
                    mNotesAdapter = new NotesAdapter(MainActivity.this, mNotesList);
                    mRecyclerView.setAdapter(mNotesAdapter);
                } else {
                    mNotesAdapter.notifyDataSetChanged();
                }
            }
        };
        mViewModel = ViewModelProviders.of(this)
                .get(ListActivityViewModel.class);

        mViewModel.mNotesList.observe(MainActivity.this, notesObserver);
    }

//    private void showData() {
//        mNotesAdapter = new NotesAdapter(this, mNotesList);
//        mRecyclerView.setAdapter(mNotesAdapter);
//    }

    private void initRecyclerView() {
        mRecyclerView.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteNote(mNotesAdapter.getNoteAtPosition(viewHolder.getAdapterPosition()));
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void deleteNote(NoteEntity noteEntity) {
        mViewModel.deleteNote(noteEntity);
        Toast.makeText(this, "Note Delete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_sample_data: {
                addSampleData();
                return true;
            }
            case R.id.delete_all_data: {
                deleteAllData();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllData() {
        mViewModel.deleteAllData();
    }

    private void addSampleData() {
        mViewModel.addSampleData();
    }
}