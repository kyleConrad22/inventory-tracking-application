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
        this.fetchRusalInventoryItems();
    }

    fetchRusalInventoryItems() {
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

    handleSubmit(evt) {
        evt.preventDefault();
        fetch("/api/rusal", {
            method: "POST",
            body: new FormData(evt.target)
        }).then((response) => {
            if (response.ok) {
                this.fetchRusalInventoryItems();
            } else {
                alert("Failed to create new inventory item");
            }
        }).catch((error) => {
            // Network errors
            alert(error);
        });
        evt.target.reset();
        return false;
    }

    render() {
        return (
            <div id = "rusal-all">
                <h1>Rusal Inventory Items</h1>
                <RusalLineItemList rusalLineItems={ this.state.rusalLineItems }/>
                <form onSubmit={ this.handleSubmit.bind(this) }>
                    <input id="heatNum" name="heatNum" type="text" placeholder="Enter Heat Number"/>
                    <input id="packageNum" name="packageNum" type="text" placeholder="Enter Package Number"/>
                    <input id="grossWeightKg" name="grossWeightKg" type="text" placeholder="Enter Gross Weight Kg"/>
                    <input id="netWeightKg" name="netWeightKg" type="text" placeholder="Enter Net Weight"/>
                    <input id="quantity" name="quantity" type="text" placeholder="Enter Quantity"/>
                    <input id="dimension" name="dimension" type="text" placeholder="Enter Dimension"/>
                    <input id="grade" name="grade" type="text" placeholder="Enter Grade"/>
                    <input id="certificateNum" name="certificateNum" type="text" placeholder="Enter Certificate Number"/>
                    <input id="blNum" name="blNum" type="text" placeholder="Enter BL Number"/>
                    <input id="barcode" name="barcode" type="text" placeholder = "Enter Barcode"/>
                    <button type="submit">Add New Item</button>
                </form>
            </div>
        );
    }
}

ReactDOM.render (
    <App />,
    document.getElementById('react-mountpoint')
)