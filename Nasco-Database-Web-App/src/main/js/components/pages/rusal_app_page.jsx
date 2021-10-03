import React, { useEffect, useState } from "react";
import RusalLineItemList from "../apps/rusal_app/rusal_line_item_list";
import "../../../css/main.css";
import RusalAddNewForm from "../apps/rusal_app/rusal_add_new_form";

export default function RusalPage() {

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
            <RusalLineItemList rusalLineItems={ rusalLineItems } />
            <RusalAddNewForm handleSubmit={ handleSubmit } />
        </div>
    );
}