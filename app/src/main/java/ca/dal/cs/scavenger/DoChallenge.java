package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DoChallenge extends AppCompatActivity implements ItemOnClickListener {

    private static final int COMPLETE_TASK_RESULT = 1;

    Challenge mChallenge;
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_challenge);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mChallenge = (Challenge)bundle.getSerializable("challenge");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTaskAdapter = new TaskAdapter(mChallenge.tasks, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mTaskAdapter);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Do challenge");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case COMPLETE_TASK_RESULT:
                handleCreateNewTaskResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void handleCreateNewTaskResult(int resultCode, Intent intent) {
        int taskIndex = mChallenge.tasks.size() - 1;

        if (resultCode == RESULT_OK) {
            // User updated the task
            Bundle bundle = intent.getExtras();
            Task updatedTask = (Task)bundle.getSerializable("task");

            mChallenge.tasks.set(taskIndex, updatedTask);

            mTaskAdapter.notifyItemChanged(taskIndex);
        } else if (resultCode == RESULT_CANCELED){
            // User did not complete the task -> do nothing
        }
    }

    @Override
    public void itemClicked(View view, int itemIndex) {
        Task task = mChallenge.tasks.get(itemIndex);
        Intent intent = Task.getIntentForCompletion(this, task.type);

        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);

        intent.putExtras(bundle);
        startActivityForResult(intent, COMPLETE_TASK_RESULT);
    }
}
