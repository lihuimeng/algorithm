package easy;

/**
 * @author Ryan Lee
 * @version $ MajorityElement, v 0.1 2026/7/22 18:18 Ryan Lee Exp $
 * @Description
 *
 * 给定一个大小为 n 的数组 nums ，返回其中的多数元素。多数元素是指在数组中出现次数 大于 ⌊ n/2 ⌋ 的元素。
 *
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 *
 *
 * 示例 1：
 *
 * 输入：nums = [3,2,3]
 * 输出：3
 * 示例 2：
 *
 * 输入：nums = [2,2,1,1,1,2,2]
 * 输出：2
 */
public class MajorityElement {

    public static void main(String[] args) {
        int[] nums = {2,2,1,1,1,2,2};
        System.out.println(majorityElement(nums));
    }


    /**
     * 摩尔投票法 找出最多的数
     * @param nums
     * @return
     */
    public static int majorityElement(int[] nums) {
        if (null == nums) {
            return 0;
        }

        int tmp = nums[0];
        int count = 0;

        for (int num : nums) {
            if (num == tmp) {
                count++;
                continue;
            }
            count--;

            if (count == 0) {
                tmp = num;
                count++;
            }
        }
        return tmp;
    }
}
