'use strict'

import React, { Component } from 'react';
import FeedbackList from './feedback';
import * as func from './functions';
import { fileUpload, formatCamelCaseToText, formatDate, getFromAPI, loader } from './../../functions';
import { grades, dbSubmissionTypeMap, dbSubmissionTypes } from './../../enums';
import moment from "moment";

export default class Submission extends Component {
    constructor(props) {
        super(props);
        this.state = {
            reportData: {},
            submissionData: {},
            feedbackPopup: false,
            file: null,
            notificationMsg: null,
            errorMsg: null,
            actionInProgress: false,
            initialReportFinished: false,
            isLoaded: false,
        };

        this.onFormSubmit = this.onFormSubmit.bind(this);
        this.onChangeFile = this.onChangeFile.bind(this);
    }

    onFormSubmit(e) {
        e.preventDefault(); // Stop form submit

        this.setState({ actionInProgress: true });
        fileUpload(this.state.file, dbSubmissionTypeMap.get(this.props.type))
            .then(() => {
                this.setMsg("File '" + this.state.file.name + "' uploaded", false)
                document.getElementById(this.props.type).value = "";
                this.setState({ actionInProgress: false, file: null })
                this.getReportData();
            })
            .catch(error => {
                this.setState({ actionInProgress: false })
                this.setMsg(error.toString(), true)
            });
    }
    onChangeFile(e) {
        this.setState({ file: e.target.files[0] })
    }

    componentDidMount() {
        this.getReportData();
    }

    setMsg(text, isError) {
        isError ? this.setState({ errorMsg: text }) : this.setState({ notificationMsg: text })
        setTimeout(() => {
            this.setState({ notificationMsg: null, errorMsg: null });
        }, 5000);
    }

    getReportData() {
        getFromAPI("/student/" + this.props.type).then(reportResponse => {
            if (this.state.reportData.submissionId) { // prevents the other API call that gets all submissions if report has no submissionId
                func.getSubmissionData(this.state.reportData.submissionId)
                .then(submissionResponse => {
                    this.setState({ submissionData: submissionResponse.entity, isLoaded: true });
                })
            }
            else {
                this.setState({ reportData: reportResponse.entity, isLoaded: true });
            }
            
            if (dbSubmissionTypeMap.get(this.props.type) == dbSubmissionTypes.initialReport) { // if initial report, check if deadline for final report has been set 
                getFromAPI("/student/finalReport").then(response => {
                    if (response.entity.deadLine) {
                        this.setState({ initialReportFinished: true })
                    }
                });
            }
        })
    }

    setFeedbackPopup(state) {
        // only open popup if feedback data exists
        if (this.state.reportData.feedBackId || (this.state.reportData.feedBackIds && this.state.reportData.feedBackIds.length > 0)) {
            this.setState({ feedbackPopup: state });
        }
    }

    render() {
        let statusPrint, deadlinePrint, gradePrint, styleClass, deadlineStyle;
        let currentDate = moment();

        // report graded - counted as finished
        if (this.state.reportData.grade != grades.NOGRADE && this.props.type != dbSubmissionTypes.initialReport) {
            statusPrint = "Status: Graded";
            gradePrint = "Grade: " + func.capitalizeFirstLetter(this.state.reportData.grade);
            styleClass = "finished";
        }
        // deadline is set (but not graded) == report counted as active
        else if (this.state.reportData.deadLine) {
            if (currentDate > moment(this.state.reportData.deadLine) && this.state.submissionData.fileUrl) {
                statusPrint = "Status: Submitted. Awaiting response";
                deadlinePrint = "Deadline: " + formatDate(this.state.reportData.deadLine);
                styleClass = "active";
            }
            else {
                // set status for initial report
                if (this.state.initialReportFinished && this.state.reportData.submissionId) {
                    statusPrint = "Status: Reviewed";
                    styleClass = "finished";
                }
                else {
                    statusPrint = "Status: " + (this.state.submissionData && this.state.submissionData.fileUrl ? "Submitted" : "Not submitted");
                    deadlinePrint = "Deadline: " + formatDate(this.state.reportData.deadLine);
                    styleClass = "active";
                }
            }
            if (currentDate > moment(this.state.reportData.deadLine)) {
                deadlineStyle = { color: "#bbb" };
            }
        }
        else {
            statusPrint = "N/A"
            styleClass = "disabled";
        }

        return (
            <div>
                <div className={"submission " + styleClass}>
                    <div className="header">{formatCamelCaseToText(this.props.type)}</div>
                    <div className="content">
                        {!this.state.isLoaded
                            ? loader
                            : this.state.reportData.deadLine != null // finished or active report
                                ?
                                <div>
                                    {this.state.submissionData.fileUrl
                                        ?
                                        <div>
                                            Uploaded file: <a href={this.state.submissionData.fileUrl} className="underscored">{this.state.submissionData.filename}</a>
                                        </div>
                                        : null
                                    }

                                    {statusPrint}
                                    <br />
                                    {gradePrint}
                                    <div style={deadlineStyle}>
                                        {deadlinePrint}
                                    </div>

                                    {/* has feedback */}
                                    {this.state.reportData.feedBackId || (this.state.reportData.feedBackIds && this.state.reportData.feedBackIds.length > 0)
                                        ? <i style={{ fontSize: "24px" }} className="far fa-comment-alt right link" onClick={() => this.setFeedbackPopup(true)} title="This report has got feedback (click to show)" />
                                        : null
                                    }

                                    {/* show file upload for active submission */}
                                    {currentDate < moment(this.state.reportData.deadLine) && this.state.reportData.grade == grades.NOGRADE && !this.state.initialReportFinished
                                        ?
                                        <div>
                                            <p style={{ fontSize: "12px" }}>
                                                {this.state.submissionData.fileUrl ? "You have already submitted a document. Submitting a new document will overwrite the old one" : null}
                                            </p>
                                            <br />
                                            <form onSubmit={this.onFormSubmit}>
                                                <input disabled={this.state.actionInProgress} type="file" id={this.props.type} onChange={this.onChangeFile} />
                                                <br />
                                                <button type="submit" disabled={this.state.actionInProgress || !this.state.file}>
                                                    {this.state.actionInProgress
                                                        ? <div>Uploading <i className="fa fa-spinner fa-spin" /></div>
                                                        : "Upload"
                                                    }
                                                </button>
                                            </form>
                                        </div>
                                        :
                                        // deadline passed
                                        currentDate > moment(this.state.reportData.deadLine) && !this.state.submissionData.fileUrl
                                            ?
                                            <div style={{ "color": "red" }}>
                                                <br />
                                                Submission deadline has passed. If you missed it, contact your coordinator to open up the submission again
                                        </div>
                                            : null
                                    }
                                </div>
                                : statusPrint // submission not started
                        }
                        <br />
                        <div style={{ color: "green" }}>
                            {this.state.notificationMsg}
                        </div>

                        <div style={{ color: "red" }}>
                            {this.state.errorMsg}
                        </div>
                    </div>
                </div>

                {/* Feedback popup */}
                {this.state.feedbackPopup
                    ?
                    <div className="popupOverlay">
                        <div className="innerPopup">
                            <i className="fas fa-window-close link right" onClick={() => this.setFeedbackPopup(false)} title="Close" />
                            <FeedbackList reportData={this.state.reportData} type={this.props.type} />
                        </div>
                    </div>
                    : null
                }
            </div>
        )

    }
}
