import React from "react";

const RusalAddNewForm = ({ handleSubmit }) => {
    return (
        <div>
            <h3>Add New Rusal Item</h3>
            <form onSubmit={ handleSubmit }>
                <input id="heat-number" name="heatNum" type="text" placeholder="Enter Heat Number"/>
                &nbsp;&nbsp;&nbsp;
                <input id="package-number" name="packageNum" type="text" placeholder="Enter Package Number"/>
                &nbsp;&nbsp;&nbsp;
                <input id="gross-weight-kg" name="grossWeightKg" type="text" placeholder="Enter Gross Weight Kg"/>
                &nbsp;&nbsp;&nbsp;
                <input id="net-weight-kg" name="netWeightKg" type="text" placeholder="Enter Net Weight"/>
                &nbsp;&nbsp;&nbsp;
                <input id="quantity" name="quantity" type="text" placeholder="Enter Quantity"/>
                &nbsp;&nbsp;&nbsp;
                <input id="dimension" name="dimension" type="text" placeholder="Enter Dimension"/>
                &nbsp;&nbsp;&nbsp;
                <input id="grade" name="grade" type="text" placeholder="Enter Grade"/>
                &nbsp;&nbsp;&nbsp;
                <input id="certificate-number" name="certificateNum" type="text" placeholder="Enter Certificate Number"/>
                &nbsp;&nbsp;&nbsp;
                <input id="bl-number" name="blNum" type="text" placeholder="Enter BL Number"/>
                &nbsp;&nbsp;&nbsp;
                <input id="barcode" name="barcode" type="text" placeholder = "Enter Barcode"/>
                &nbsp;&nbsp;&nbsp;
                <button type="submit">Add New Item</button>
            </form>
        </div>
    );
}

export default RusalAddNewForm;