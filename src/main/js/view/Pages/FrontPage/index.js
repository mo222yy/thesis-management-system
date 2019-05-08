'use strict';

import React, { Component } from 'react'
const client = require('../../../client');

class FrontPage extends Component {
    constructor(props) {
        super(props);
        this.state = { users: [] };
    }

    componentDidMount() {
        client({ method: 'GET', path: '/users' }).then(response => {
            this.setState({ users: response.entity._embedded.users });
        });
    }

    render() {
        return (
            <div>
                <p>FrontPage</p>
            </div>
        )
    }
}

export default FrontPage