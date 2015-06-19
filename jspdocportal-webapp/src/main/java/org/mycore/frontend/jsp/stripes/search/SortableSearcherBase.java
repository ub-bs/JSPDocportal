/*
 * $RCSfile$
 * $Revision: 19696 $ $Date: 2011-01-04 13:45:05 +0100 (Di, 04 Jan 2011) $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package org.mycore.frontend.jsp.stripes.search;

import java.util.Arrays;

/**
 * base class for a searcher with sort features
 * 
 * @author Robert Stephan
 *
 */
public abstract class SortableSearcherBase extends SearcherBase {
	private String sortiere_nach = "modified";
	private String sortiere_wie = "desc";

	public SortableSearcherBase() {
		super();
	}

	public SortableSearcherBase(String solrBaseURL, String searchmaskURL) {
		super(solrBaseURL, searchmaskURL);
	}

	public void deepCopy(SortableSearcherBase result) {
		result.setSortiere_nach(getSortiere_nach());
		result.setSortiere_wie(getSortiere_wie());
		super.deepCopy(result);
	}

	public String getSortiere_nach() {
		return sortiere_nach;
	}

	public void setSortiere_nach(String sortBy) {
		this.sortiere_nach = sortBy;
	}

	public String getSortiere_wie() {
		return sortiere_wie;
	}

	public void setSortiere_wie(String sortOrder) {
		this.sortiere_wie = sortOrder;
	}

	/**
	 * finish this method with super.reset()!
	 */
	public void reset() {
		sortiere_nach = "modified";
		sortiere_wie = "desc";
		super.reset();
	}

	@Override
	public String getGeneratedSortQuery() {
		if (!Arrays.asList(new String[] { "date", "modified", "author_sort", "title_sort" })
		        .contains(sortiere_nach)) {
			sortiere_nach = "date";
		}
		if (!Arrays.asList(new String[] { "asc", "desc" }).contains(sortiere_wie)) {
			sortiere_wie = "desc";
		}
		return sortiere_nach + "+" + sortiere_wie;
	}
}
