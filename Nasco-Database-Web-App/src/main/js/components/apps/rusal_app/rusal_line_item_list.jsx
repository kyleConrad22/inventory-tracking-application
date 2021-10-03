import React, { Component } from "react";
import RusalLineItem from "./rusal_line_item";

export default function RusalLineItemList(props) {

    if (!props.rusalLineItems) {
        return <div>No Rusal Items Found...</div>
    }

    const rusalLineItems = props.rusalLineItems.map(rusalLineItem => 
        <RusalLineItem key={rusalLineItem.heatNum} rusalLineItem={rusalLineItem}/>
    );

    return (
        <table className='padded-table'>
            <tbody>
                <tr className='bordered-row'>
                    <th>Heat Number</th>
                    <th>Gross Weight Kg</th>
                    <th>Net Weight Kg</th>
                    <th>BL Number</th>
                </tr>
                {rusalLineItems}
            </tbody>
        </table>
    );
}