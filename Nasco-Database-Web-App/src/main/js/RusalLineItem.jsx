import React, { Component } from "react";

class RusalLineItem extends Component {
    render() {
        return (
            <tr>
                <td>{this.props.rusalLineItem.heatNum}</td>
                <td>{this.props.rusalLineItem.grossWeightKg}</td>
                <td>{this.props.rusalLineItem.netWeightKg}</td>
                <td>{this.props.rusalLineItem.blNum}</td>
            </tr>
        )
    }
}

export default RusalLineItem;