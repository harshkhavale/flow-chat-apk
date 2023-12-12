package com.hashcode.Flow.Models;

public class MediaModel {
    String DisplayName,Path,Number,Image,Video,Length,Size,Type;
    Boolean Checked = false;

    public MediaModel(String displayName, String path, String number, String image, String video, String length, String size, Boolean checked) {
        DisplayName = displayName;
        Path = path;
        Number = number;
        Image = image;
        Video = video;
        Length = length;
        Size = size;
        Checked = checked;
    }

    public MediaModel() {
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public Boolean getChecked() {
        return Checked;
    }

    public void setChecked(Boolean checked) {
        Checked = checked;
    }
}
