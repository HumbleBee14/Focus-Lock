package com.grepguru.focuslock.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grepguru.focuslock.R;
import com.grepguru.focuslock.model.AppModel;
import java.util.List;

public class AllowedAppsAdapter extends RecyclerView.Adapter<AllowedAppsAdapter.ViewHolder> {
    private List<AppModel> allowedApps;
    private Context context;

    public AllowedAppsAdapter(Context context, List<AppModel> allowedApps) {
        this.context = context;
        this.allowedApps = allowedApps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allowed_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = allowedApps.get(position);
        holder.appIcon.setImageDrawable(app.getIcon());
        holder.appName.setText(app.getAppName());

        // Launch the app when clicked
        holder.itemView.setOnClickListener(v -> {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            if (launchIntent != null) {
                context.startActivity(launchIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allowedApps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;

        public ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
        }
    }
}
