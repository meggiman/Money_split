package ch.ethz.itet.pps.budgetSplit;

import android.database.Cursor;

import java.util.Iterator;


/** Class to replace CursorJoiner as there appears to be a defect in it, possibly
 * due to the string comparison being done. This class should act identical to the 
 * CursorJoiner class with the exception of the aforementioned defect.
 * 
 * @see: http://code.google.com/p/android/issues/detail?id=3707
 */
public class MyCursorJoiner implements Iterator<MyCursorJoiner.Result>, Iterable<MyCursorJoiner.Result> {
	
	private 	Cursor 		mCursorLeft;
	private 	Cursor 		mCursorRight;
	private 	String[] 	mKeysLeft;
	private 	String[] 	mKeysRight;
	private 	int[] 		mColumnIxLeftKey;
	private 	int[] 		mColumnIxRightKey;
	private 	Result 		mPreviousResult = null;
	private		boolean		mCursorsNeedAdvance = false;

	@Override
	public Iterator<MyCursorJoiner.Result> iterator() {
		return this;
	}

	/** The result of a call to next(). */
	public static enum Result {
       /** The key in the left cursor is unique. */
		RIGHT,
	   /** The key in the right cursor is unique. */
		LEFT,
	   /** The keys in both cursors are the same */
		BOTH
   }
	
	
	/** Replaces android.database.CursorJoiner as that module doesn't seem to work.
	 * 
	 * @param cursorLeft 	- left cursor
	 * @param keysLeft 		- left key array
	 * @param cursorRight 	- right cursor
	 * @param keysRight 	- right key array
	 * 
	 * @throws IllegalArgumentException - if any parm is null, or any cursor 
	 * 		is not open, or either of leftKeys.length or righKeys.length < 1 or 
	 * 		leftKeys.length != rightKeys.length.
	 */
	public MyCursorJoiner(Cursor cursorLeft, String[] keysLeft, Cursor cursorRight, String[] keysRight) {
		
        if (keysLeft.length != keysRight.length) {
            throw new IllegalArgumentException(
                    "you must have the same number of columns on the left and right, "
                            + keysLeft.length + " != " + keysRight.length);
        }
        
		if (cursorRight == null || keysRight == null)
			throw new IllegalArgumentException("Null cursor not supported");
		
		if (cursorLeft.isClosed() || cursorRight.isClosed())
			throw new IllegalArgumentException("Cursor must be open for both tables");
			
		if (keysLeft.length < 1 || keysRight.length < 1 )
			throw new IllegalArgumentException("key column(s) must be supplied for both tables");
		
		mCursorLeft 	= cursorLeft;
		mCursorRight 	= cursorRight;
		mKeysLeft 		= keysLeft;
		mKeysRight 		= keysRight;
		
		mCursorLeft.moveToFirst();
		mCursorRight.moveToFirst();
		
		setColumnIndicies();
	}

	/** sets the column indexes for each of the 2 tables.
	 */
	public void setColumnIndicies() {
		
		// get left key columns & right key columns (both cursors must have same # and type of keys)
		mColumnIxLeftKey = new int[mKeysLeft.length];
		mColumnIxRightKey = new int[mKeysRight.length];
		for (int i=0; i<mKeysLeft.length; i++) {
			mColumnIxLeftKey[i] = mCursorLeft.getColumnIndex(mKeysLeft[i]);
			mColumnIxRightKey[i] = mCursorRight.getColumnIndex(mKeysRight[i]);
		}
	}
	
	/** this needs to be conscious of the previousResult.advanceCursor problem.
	 * That is, this needs to advance the cursors first if they've not been.
	 * 
	 * @return true if there are more records to read
	 */
	public boolean hasNext() {
		
		advanceCursors(mPreviousResult);
		return !(mCursorLeft.isAfterLast() && mCursorRight.isAfterLast() );
	}
	
	
	/** handles next() processing
	 * 
	 * @return result of BOTH, LEFT, RIGHT.
	 */
    public Result next() {
    	
        if (!hasNext()) {
            throw new IllegalStateException("hasNext() is false");
        } 
        
       	if (mPreviousResult != null)
        	advanceCursors(mPreviousResult);
       	
        mCursorsNeedAdvance = true;
        
        return (mPreviousResult = compareCursorKeys());
    }

	@Override
	public void remove() {
		throw new UnsupportedOperationException("MyCursorJoiner does not support remove.");
	}


	/** advances the cursors as necessary from the result */
    private void advanceCursors(Result result) {
    	
		if (mCursorsNeedAdvance) {	    	
	        switch (result) {
	        	case BOTH:
	        		mCursorLeft.moveToNext();
	        		mCursorRight.moveToNext();
	        		break;
	        		
	        	case LEFT:
	        		mCursorLeft.moveToNext();
	        		break;
	        		
	        	case RIGHT:
	        		mCursorRight.moveToNext();
	        		break;
	        }    	
		}
        mCursorsNeedAdvance = false;
    }

    /** compares the two cursors key columns.
     * 
     * @return @see:Result
     */
    private Result compareCursorKeys() {
    	
    	Result result = Result.BOTH;
    	
        boolean hasLeft 	= !mCursorLeft.isAfterLast();
        boolean hasRight 	= !mCursorRight.isAfterLast();
        
        // already checked case of both at EOF in !hasNext(), prior to calling this method.
        
        if 		(!hasLeft) 	result = Result.RIGHT;
        else if (!hasRight) result = Result.LEFT;
        else {
            // case: neither is EOF
        	int ix = 0;
        	long leftKey = 0;
        	long rightKey = 0;
        	// while not at the end of the keys and the result is equal (BOTH)
        	while (ix<mKeysLeft.length && result == Result.BOTH) {

        		// try comparing as Long, if it fails on number format, compare as String
        		try {
        			leftKey = mCursorLeft.getLong(mColumnIxLeftKey[ix]);
        			rightKey = mCursorRight.getLong(mColumnIxRightKey[ix]);
        			
        			if (leftKey > rightKey)
        				result = Result.RIGHT;
        			else if (leftKey < rightKey)
        				result = Result.LEFT;
        			
        		} catch (NumberFormatException e){
        			
        			// compare as string if Long fails
            		int c = mCursorLeft.getString(mColumnIxLeftKey[ix])
    							.compareTo(mCursorRight.getString(mColumnIxRightKey[ix])); 
            		
            		if 		(c < 0)	result = Result.LEFT;
            		else if (c > 0)	result = Result.RIGHT;
        		}
        		ix++;
        	}        	
        }
        
        return result;
    }
	
}
