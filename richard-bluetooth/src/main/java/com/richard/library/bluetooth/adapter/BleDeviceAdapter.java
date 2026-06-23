package com.richard.library.bluetooth.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richard.library.bluetooth.R;
import com.richard.library.bluetooth.core.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Description : 蓝牙设备列表适配
 * Author : Richard
 * Date : 2023/4/16 08:59
 * Changelog: 无
 * </pre>
 */
public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleViewHolder> {

    private final List<BleDevice> data = new ArrayList<>();
    private Callback callback;


    @NonNull
    @Override
    public BleDeviceAdapter.BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_ble_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceAdapter.BleViewHolder holder, int position) {
        BleDevice itemInfo = data.get(position);
        holder.tvName.setText(itemInfo.getName());
        holder.tvAddress.setText(String.format("%s (%s)", itemInfo.getDevice().getAddress(), itemInfo.getTypeName()));
        holder.ivIcon.setImageTintList(ColorStateList.valueOf(itemInfo.isBonded() ? Color.GREEN : Color.BLUE));
        holder.tvRssi.setText(String.format("%s dBm", itemInfo.getRssi()));

        if (callback != null) {
            holder.itemView.setOnClickListener((v) -> {
                callback.onItemClick(itemInfo);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 清空列表
     */
    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

    /**
     * 批量更新蓝牙设备列表数据项
     */
    public void update(List<BleDevice> deviceList) {
        if (deviceList == null || deviceList.isEmpty()) {
            return;
        }

        for (BleDevice item : deviceList) {
            this.update(item, false);
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * 更新列表数据项
     */
    public void update(BleDevice device) {
        this.update(device, true);
    }

    /**
     * 更新列表数据项
     */
    public void update(BleDevice device, boolean isNotifyChange) {
        if (device == null) {
            return;
        }

        int index = this.isHasDevice(device.getDevice());
        if (index >= 0) {
            data.set(index, device);
            if (isNotifyChange) {
                notifyItemChanged(index);
            }
        } else {
            data.add(device);
            if (isNotifyChange) {
                notifyItemInserted(data.size() - 1);
            }
        }
    }

    /**
     * 判断列表中是否已存在蓝牙设备
     *
     * @return -1：不存在、大于等于0代表存在，同时也是存在的下标位置
     */
    public int isHasDevice(BluetoothDevice device) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getDevice().getAddress().equals(device.getAddress())) {
                return i;
            }
        }
        return -1;
    }

    public static class BleViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvAddress;
        ImageView ivIcon;
        TextView tvRssi;

        public BleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvRssi = itemView.findViewById(R.id.tv_rssi);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onItemClick(BleDevice device);
    }
}
