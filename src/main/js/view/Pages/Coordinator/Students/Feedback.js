import React, { Component } from "react";
import { getName, capitalizeFirstLetter } from "../functions";
import * as Style from "../Styles";

class Feedback extends Component {

  render() {
    console.log('feedbackprops', this.props)
    return (
      <div>
        <div style={Style.submissionFeedbackRow}>
          <span style={Style.submissionLeftColumn}>Feedback</span>
          <span style={Style.submissionRightColumn}>
            <textarea
              style={Style.textarea}
              onChange={() => this.props.onFeedbackChange(event)}
              value={this.props.feedback.text}
            />
          </span>
        </div>
        {this.props.feedback !== null ? (
          <div style={Style.submissionFeedbackFromRow}>
            <span style={Style.submissionLeftColumn}>From</span>
            <span style={Style.submissionRightColumn}>
              {capitalizeFirstLetter(this.props.feedback.role)}:{" "}
              {getName(this.props.feedback.userId)}
            </span>
          </div>
        ) : null}
      </div>
    );
  }
}

export default Feedback;