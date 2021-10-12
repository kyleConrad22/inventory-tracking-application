/* eslint-disable react/prop-types */
import React from "react";

export default function RusalLineItem(props) {
    
    return (
        <tr className='bordered-row'>
            <td>{props.rusalLineItem.heatNum}</td>
            <td>{props.rusalLineItem.grossWeightKg}</td>
            <td>{props.rusalLineItem.netWeightKg}</td>
            <td>{props.rusalLineItem.grade}</td>
            <td>{props.rusalLineItem.dimension}</td>
            <td>{props.rusalLineItem.quantity}</td>
            <td>{props.rusalLineItem.blNum}</td>
            <td>{props.rusalLineItem.workOrder}</td>
            <td>{props.rusalLineItem.loadNum}</td>
        </tr>
    );
}