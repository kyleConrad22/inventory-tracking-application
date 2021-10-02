import React from "react";
import { useHistory } from "react-router";
import ToBeImplemented from "../util/to_be_implemented";

export default function MiscFunctionsPage() {
    function EditCustomerProcess() {
        let history = useHistory();

        function handleClick() {
            history.push("")
        }
    }

    return (
        <div>
            <h1>Misc Functions Landing Page</h1>
            <ToBeImplemented />
        </div>
    );
}