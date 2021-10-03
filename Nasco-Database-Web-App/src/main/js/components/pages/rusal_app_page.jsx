import React, { useEffect, useState } from "react";
import RusalLineItemList from "../apps/rusal_app/rusal_line_item_list";
import "../../../css/main.css";

export default function RusalPage(props) {

    const [rusalLineItems, setRusalLineItems] = useState([]);
    
    useEffect(() => {
        fetchRusalInventoryItems();
    }, []);

    function fetchRusalInventoryItems() {
        fetch("/api/rusal")
            .then(res => res.json())
            .then(
                (response) => {
                    setRusalLineItems(response);
                },
                (error) => {
                    alert(error);
                }
            )
    }

    function handleSubmit(evt) {
        evt.preventDefault();
        fetch("/api/rusal", {
            method: "POST",
            body: new FormData(evt.target)
        }).then(
            (response) => {
                if (response.ok) {
                    fetchRusalInventoryItems();
                } else {
                    alert("Failed to create new Rusal item");
                }
            }).catch(
                (error) => {
                    // Network errors
                    alert(error)
            });
            evt.target.reset();
            return false;
    }

    return (
        <div id = "rusal-all">
                <h1>Rusal Inventory Items</h1>
                <RusalLineItemList rusalLineItems={ rusalLineItems }/>
                <form onSubmit={ handleSubmit }>
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