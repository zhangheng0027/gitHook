package com.zh.commit;

import org.kohsuke.args4j.Option;

public class PostCommitOption {

    @Option(name="-branch", required=true)
    private String branch;

    @Option(name="-md5", required=true)
    private String md5;

    @Option(name="-author", required=true)
    private String author;

    @Option(name="-mail", required=true)
    private String mail;

    @Option(name="-date", required=true)
    private String date;

    @Option(name="-msg", required=true)
    private String msg;

    @Option(name="-fileName", required=true)
    private String fileName;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
