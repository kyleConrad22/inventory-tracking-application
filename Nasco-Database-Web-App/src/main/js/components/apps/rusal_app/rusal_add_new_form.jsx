import React from "react";

const RusalAddNewForm = ({ handleSubmit }) => {
    return (
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
    );
}

export default RusalAddNewForm;