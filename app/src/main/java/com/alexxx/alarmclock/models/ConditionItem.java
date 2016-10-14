package com.alexxx.alarmclock.models;

public class ConditionItem {
    private int mImgId;
    private String mText;
    private boolean mIsChecked;
    private Integer nCode;

    public ConditionItem(int imgId, String text, int code) {
        this.setImgId(imgId);
        this.setText(text);
        this.setCode(code);
    }

    public int getImgId() {
        return mImgId;
    }

    private void setImgId(int imgId) {
        this.mImgId = imgId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    public Integer getCode() {
        return nCode;
    }

    private void setCode(Integer code) {
        this.nCode = code;
    }
}
