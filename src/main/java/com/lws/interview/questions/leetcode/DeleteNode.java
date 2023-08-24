package com.lws.interview.questions.leetcode;

public class DeleteNode {

    public static void main(String[] args) {
    }

    public static ListNode deleteNode(ListNode head, int val) {
        ListNode dummyHead = new ListNode(-1);
        dummyHead.next = head;
        head = dummyHead;
        while (head.next != null) {
            if (head.next.val == val) {
                head.next = head.next.next;
                return dummyHead.next;
            }else {
                head = head.next;
            }
        }
        return dummyHead.next;
    }
}
