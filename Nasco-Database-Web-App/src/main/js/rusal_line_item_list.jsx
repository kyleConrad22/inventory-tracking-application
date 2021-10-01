import React, { Component } from "react";
import RusalLineItem from "./RusalLineItem";

class RusalLineItemList extends Component {
    render() {
        if (!this.props.rusalLineItems) {
            return <div>No Rusal Items Found...</div>
        }

        const rusalLineItems = this.props.rusalLineItems.map(rusalLineItem => 
            <RusalLineItem key={rusalLineItem.heatNum} rusalLineItem={rusalLineItem}/>
        );

        return (
            <table>
                <tbody>
                    <tr>
                        <th>Heat Number</th>
                        <th>Gross Weight Kg</th>
                        <th>Net Weight Kg</th>
                        <th>BL Number</th>
                    </tr>
                    {rusalLineItems}
                </tbody>
            </table>
        )
    }
}

export default RusalLineItemList;