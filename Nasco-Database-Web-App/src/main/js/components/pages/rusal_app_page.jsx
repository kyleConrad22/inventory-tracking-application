import React, { useEffect, useState } from "react";
import { Switch, Link , Route, useRouteMatch} from "react-router-dom";
import RusalLineItemList from "../apps/rusal_app/rusal_line_item_list";
import "../../../css/main.css";
import RusalAddNewForm from "../apps/rusal_app/rusal_add_new_form";
import RusalDownloads from "../apps/rusal_app/rusal_downloads";
import RusalUpdateItem from "../apps/rusal_app/rusal_update_item";
import RusalUploadPackingList from "../apps/rusal_app/rusal_upload_packing_list";

export default function RusalPage() {

    let { path, url } = useRouteMatch();

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
            <ul>
                <li>
                    <Link to={`${url}/add`}>Add Inventory Items</Link>
                </li>
                <li>
                    <Link to={`${url}/update`}>Update Inventory Item</Link>
                </li>
                <li>
                    <Link to={`${url}/downloads`}>Download Options</Link>
                </li>
            </ul>
            <Switch>
                <Route exact path={path}>
                    <h3>Please Choose a Function</h3>
                </Route>
                <Route path={`${path}/add`}>
                    <RusalAddNewForm handleSubmit={ handleSubmit } /> 
                    <RusalUploadPackingList />
                </Route>
                <Route path={`${path}/update`}>
                    <RusalUpdateItem />
                </Route>
                <Route path={`${path}/downloads`}>
                    <RusalDownloads />
                </Route>
            </Switch>
        </div>
    );
}