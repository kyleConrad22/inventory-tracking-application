import React from "react";

export default function RusalLineItem(props) {
    
    return (
        <tr>
            <td>{props.rusalLineItem.heatNum}</td>
            <td>{props.rusalLineItem.grossWeightKg}</td>
            <td>{props.rusalLineItem.netWeightKg}</td>
            <td>{props.rusalLineItem.blNum}</td>
        </tr>
    );
}