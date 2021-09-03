/**
 * utility functions
 */
class Utils {
  /**
   * return fixed number.
   * @param {number} value
   * @param {number} fraction
   * @return {unknown}
   */
  static toFixedFloor(value, fraction=2) {
    const base = Math.pow(10, fraction);
    return (Math.floor(value * base) / base).toFixed(fraction);
  }
}

export {Utils};
