import React, { Component } from "react";
import StudentsTable from "./StudentsTable";
import * as func from "../func";

class Students extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <div>
          <StudentsTable/>
        </div>
      </div>
    );
  }
}

export default Students;
