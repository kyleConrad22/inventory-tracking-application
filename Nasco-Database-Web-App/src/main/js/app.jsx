import React, { Component } from "react";
import ReactDOM from "react-dom";
import RusalLineItemList from "./rusal_line_item_list";
import "../css/main.css"

class App extends Component {
    constructor(props) {
        super(props)
        this.state = { rusalLineItems : [] }
    }

    componentDidMount() {
        fetch("/api/rusal")
            .then(res => res.json())
            .then(
                (response) => {
                    this.setState({
                        rusalLineItems : response
                    });
                },
                (error) => {
                    alert(error);
                }
            )
    }

    render() {
        return (
            <div>
                <h1>Rusal Line Items</h1>
                <RusalLineItemList rusalLineItems={ this.state.rusalLineItems }/>
            </div>
        );
    }
}

ReactDOM.render (
    <App />,
    document.getElementById('react-mountpoint')
)