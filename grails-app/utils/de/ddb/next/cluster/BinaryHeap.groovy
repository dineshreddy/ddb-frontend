package de.ddb.next.cluster

class BinaryHeap {

    def content
    //    def scoreFunction

    //    def BinaryHeap(scoreFunction) {
    //        this.content = []
    //        this.scoreFunction = scoreFunction
    //    }
    def BinaryHeap() {
        this.content = []
    }


    def push(element) {
        // Add the new element to the end of the array.
        this.content.push(element)
        // Allow it to bubble up.
        this.bubbleUp(this.content.size() - 1)
    }

    def pop() {
        // Store the first element so we can return it later.
        def result = this.content[0]
        // Get the element at the end of the array.
        def end = this.content.pop()
        // If there are any elements left, put the end element at the
        // start, and let it sink down.
        if (this.content.size() > 0) {
            this.content[0] = end
            this.sinkDown(0)
        }
        return result
    }

    def remove(node) {
        def len = this.content.size()
        // To remove a value, we must search through the array to find
        // it.
        for (def i = 0; i < len; i++) {
            if (this.content[i] == node) {
                // When it is found, the process seen in 'pop' is repeated
                // to fill up the hole.
                def end = this.content.pop()
                if (i != len - 1) {
                    this.content[i] = end
                    if (end.weight < node.weight)
                        this.bubbleUp(i)
                    else
                        this.sinkDown(i)
                }
                return
            }
        }
        throw new Exception("Node not found.")
    }

    def size() {
        return this.content.size()
    }

    def bubbleUp(n) {
        // Fetch the element that has to be moved.
        def element = this.content[n]
        // When at 0, an element can not go up any further.
        while (n > 0) {
            // Compute the parent element's index, and fetch it.
            def parentN = (int)(Math.floor((n + 1) / 2) - 1)
            def parent = this.content[parentN]
            // Swap the elements if the parent is greater.
            if (element.weight < parent.weight) {
                this.content[parentN] = element
                this.content[n] = parent
                // Update 'n' to continue at the new position.
                n = parentN
            }
            // Found a parent that is less, no need to move it further.
            else {
                break
            }
        }
    }

    def sinkDown(n) {
        // Look up the target element and its score.
        def length = this.content.size()
        def element = this.content[n]
        def elemScore = element.weight

        while (true) {
            // Compute the indices of the child elements.
            def child2N = (n + 1) * 2
            def child1N = child2N - 1
            // This is used to store the new position of the element,
            // if any.
            def swap = null
            // If the first child exists (is inside the array)...
            def child1Score
            if (child1N < length) {
                // Look it up and compute its score.
                def child1 = this.content[child1N]
                child1Score = child1.weight
                // If the score is less than our element's, we need to swap.
                if (child1Score < elemScore)
                    swap = child1N
            }
            // Do the same checks for the other child.
            if (child2N < length) {
                def child2 = this.content[child2N]
                def child2Score = child2.weight
                if (child2Score < (swap == null ? elemScore : child1Score))
                    swap = child2N
            }

            // If the element needs to be moved, swap it, and continue.
            if (swap != null) {
                this.content[n] = this.content[swap]
                this.content[swap] = element
                n = swap
            }
            // Otherwise, we are done.
            else {
                break
            }
        }
    }

}
