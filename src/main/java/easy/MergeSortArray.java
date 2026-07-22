package easy;

/**
 * @author Ryan Lee
 * @version $ MergeSortArray, v 0.1 2026/7/22 16:40 Ryan Lee Exp $
 * @Description
 * 给你两个按 非递减顺序 排列的整数数组 nums1 和 nums2，另有两个整数 m 和 n ，分别表示 nums1 和 nums2 中的元素数目。
 *
 * 请你 合并 nums2 到 nums1 中，使合并后的数组同样按 非递减顺序 排列。
 *
 * 注意：最终，合并后数组不应由函数返回，而是存储在数组 nums1 中。为了应对这种情况，nums1 的初始长度为 m + n，其中前 m 个元素表示应合并的元素，后 n 个元素为 0 ，应忽略。nums2 的长度为 n 。
 */
public class MergeSortArray {

    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        if (n == 0) {
            return;
        }

        for (int i = m+n - 1; i >= 0; i--) {
            if (m == 0) {
                nums1[i] = nums2[n-1];
                n--;
                continue;
            }
            if (n == 0) {
                break;
            }

            int i1 = nums1[m - 1];
            int i2 = nums2[n - 1];
            if (i1 > i2) {
                nums1[i] = i1;
                m--;
            } else {
                nums1[i] = i2;
                n--;
            }
        }
    }

    public static void main(String[] args) {
        int[] nums1 = new int[]{1,2,3,0,0,0};
        int[] nums2 = new int[]{2,5,6};
        merge(nums1, 3, nums2, 3);
    }
}
