'use strict';

import React from 'react';
import axios from 'axios';
import moment from "moment";
const client = require("../client");
const maxFileUploadSize = 10; //MB

export function getFromAPI(getPath) {
    return client({
        method: "GET",
        path: getPath,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
}

export function getUser(userId) {
    return getFromAPI("/users/" + userId)
}

export function putToAPI2(putPath, data) {
    return client({
        method: "PUT",
        path: putPath,
        entity: data,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
}
export function putToAPI(putPath, data) {
    return client({
        method: "PUT",
        path: putPath,
        body: JSON.stringify(data),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
}

export function postToAPI(postPath, object) {
    return client({
        method: "POST",
        path: postPath,
        entity: object,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
    });
}

export const downloadFile = async (fileUrl) => {
    let win = window.open(fileUrl, '_blank')
    win.focus
}

export function fileUpload(file, dbType) {
    if(isAboveFileUploadSize(file)) {
        return new Promise(() => {
            // reject("File size too big. Max file size is " + maxFileUploadSize + " MB")
            throw new Error("File size too big. Max file size is " + maxFileUploadSize + " MB")
          });
    }
 
    const url = '/student/newSubmission?subType=' + dbType;
    const formData = new FormData();
    formData.append('file', file);
    const config = {
        headers: {
            'content-type': 'multipart/form-data'
        }
    };
    return axios.post(url, formData, config)
}

function isAboveFileUploadSize(file) {
    return file.size / 1024 / 1024 > maxFileUploadSize;
}

export function deleteFromAPI(delPath) {
    return client({ method: "DELETE", path: delPath });
}

export function capitalizeFirstLetter(string) {
    if (string == null)
        return "N/A";
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
}

export function formatCamelCaseToText(string) {
    if (string == null)
        return "N/A";

    let text = string.charAt(0).toUpperCase() + string.slice(1);
    for (let i = 1; i < string.length; i++) {
        if (text.charAt(i) == text.charAt(i).toUpperCase()) { //char is upperCase
            text = text.slice(0, i) + " " + text.slice(i);
        }
    }
    return text;
}

export function formatDate(date) {
    return moment(date).format("MMMM Do YYYY, HH:mm");
}

export const loader = (
    <span>
        Loading <i className="fa fa-spinner fa-spin" />
    </span>
)