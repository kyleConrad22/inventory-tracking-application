/* eslint-disable react/prop-types */
import React from "react";
import RusalLineItem from "./rusal_line_item";

export default function RusalLineItemList(props) {

    if (!props.rusalLineItems) {
        return <div>No Rusal Items Found...</div>
    }

    const rusalLineItems = props.rusalLineItems.map(rusalLineItem => 
        <RusalLineItem key={rusalLineItem.heatNum} rusalLineItem={rusalLineItem}/>
    )

    return (
        <table>
            <tbody>
                <tr className='bordered-row'>
                    <th>Heat Number</th>
                    <th>Grade</th>
                    <th>Piece Count</th>
                    <th>BL Number</th>
                    <th>Barge</th>
                    <th>Reception Time</th>
                    <th>Work Order</th>
                    <th>Load Number</th>
                    <th>Shipment Time</th>
                </tr>
                {rusalLineItems}
            </tbody>
        </table>
    );
}