package ca.uhn.fhir.model.api;

/*
 * #%L
 * HAPI FHIR - Core Library
 * %%
 * Copyright (C) 2014 - 2015 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.uhn.fhir.model.primitive.BoundCodeDt;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.model.primitive.XhtmlDt;
import ca.uhn.fhir.model.valueset.BundleEntryStatusEnum;
import ca.uhn.fhir.util.ElementUtil;

public class BundleEntry extends BaseBundle {

	//@formatter:off
	/* ****************************************************
	 * NB: add any new fields to the isEmpty() method!!!
	 *****************************************************/
	//@formatter:on
	private TagList myCategories;
	private InstantDt myDeletedAt;
	private StringDt myDeletedByEmail;
	private StringDt myDeletedByName;
	private StringDt myDeletedComment;
	private StringDt myDeletedResourceId;
	private CodeDt myDeletedResourceType;
	private StringDt myDeletedResourceVersion;
	private StringDt myLinkAlternate;
	private StringDt myLinkBase;
	private StringDt myLinkSearch;
	private StringDt myLinkSelf;
	private InstantDt myPublished;
	private IResource myResource;
	private DecimalDt myScore;
	private BoundCodeDt<BundleEntryStatusEnum> myStatus;
	private XhtmlDt mySummary;
	private StringDt myTitle;
	private InstantDt myUpdated;

	/**
	 * @deprecated Tags wil become immutable in a future release of HAPI, so {@link #addCategory(String, String, String)} should be used instead
	 */
	public Tag addCategory() {
		Tag retVal = new Tag();
		getCategories().add(retVal);
		return retVal;
	}

	public void addCategory(String theScheme, String theTerm, String theLabel) {
		getCategories().add(new Tag(theScheme, theTerm, theLabel));
	}

	public void addCategory(Tag theTag) {
		getCategories().add(theTag);
	}

	public TagList getCategories() {
		if (myCategories == null) {
			myCategories = new TagList();
		}
		return myCategories;
	}

	/**
	 * Gets the date/time that thius entry was deleted.
	 */
	public InstantDt getDeletedAt() {
		if (myDeletedAt == null) {
			myDeletedAt = new InstantDt();
		}
		return myDeletedAt;
	}

	public StringDt getDeletedByEmail() {
		if (myDeletedByEmail == null) {
			myDeletedByEmail = new StringDt();
		}
		return myDeletedByEmail;
	}

	public StringDt getDeletedByName() {
		if (myDeletedByName == null) {
			myDeletedByName = new StringDt();
		}
		return myDeletedByName;
	}

	public StringDt getDeletedComment() {
		if (myDeletedComment == null) {
			myDeletedComment = new StringDt();
		}
		return myDeletedComment;
	}

	public StringDt getDeletedResourceId() {
		if (myDeletedResourceId == null) {
			myDeletedResourceId = new StringDt();
		}
		return myDeletedResourceId;
	}

	public CodeDt getDeletedResourceType() {
		if (myDeletedResourceType == null) {
			myDeletedResourceType = new CodeDt();
		}
		return myDeletedResourceType;
	}

	public StringDt getDeletedResourceVersion() {
		if (myDeletedResourceVersion == null) {
			myDeletedResourceVersion = new StringDt();
		}
		return myDeletedResourceVersion;
	}

	public StringDt getLinkAlternate() {
		if (myLinkAlternate == null) {
			myLinkAlternate = new StringDt();
		}
		return myLinkAlternate;
	}

	public StringDt getLinkBase() {
		if (myLinkBase == null) {
			myLinkBase = new StringDt();
		}
		return myLinkBase;
	}

	public StringDt getLinkSearch() {
		if (myLinkSearch == null) {
			myLinkSearch = new StringDt();
		}
		return myLinkSearch;
	}

	public StringDt getLinkSelf() {
		if (myLinkSelf == null) {
			myLinkSelf = new StringDt();
		}
		return myLinkSelf;
	}

	public InstantDt getPublished() {
		if (myPublished == null) {
			myPublished = new InstantDt();
		}
		return myPublished;
	}

	public IResource getResource() {
		return myResource;
	}

	public DecimalDt getScore() {
		if (myScore == null) {
			myScore = new DecimalDt();
		}
		return myScore;
	}

	public BoundCodeDt<BundleEntryStatusEnum> getStatus() {
		if (myStatus == null) {
			myStatus = new BoundCodeDt<BundleEntryStatusEnum>(BundleEntryStatusEnum.VALUESET_BINDER);
		}
		return myStatus;
	}

	public XhtmlDt getSummary() {
		if (mySummary == null) {
			mySummary = new XhtmlDt();
		}
		return mySummary;
	}

	public StringDt getTitle() {
		if (myTitle == null) {
			myTitle = new StringDt();
		}
		return myTitle;
	}

	/**
	 * @deprecated <b>DSTU2 Note:</b> As of DSTU2, bundle entries no longer have an updated time (this bit of metadata has been moved to the resource &lt;meta/&gt; element so it is redundant here). In
	 *             preparation for DSTU2, it is recommended that you migrate code away from using this method and over to using resource metadata instead.
	 */
	public InstantDt getUpdated() {
		if (myUpdated == null) {
			myUpdated = new InstantDt();
		}
		if (myUpdated.isEmpty() && myResource != null) {
			InstantDt resourceUpdated = ResourceMetadataKeyEnum.UPDATED.get(myResource);
			if (resourceUpdated != null && !resourceUpdated.isEmpty()) {
				return resourceUpdated;
			}
		}
		return myUpdated;
	}

	@Override
	public boolean isEmpty() {
		//@formatter:off
		return super.isEmpty() && 
				ElementUtil.isEmpty(myDeletedResourceId, myDeletedResourceType, myDeletedResourceVersion, myScore,myStatus, myCategories, myDeletedAt, myLinkAlternate, myLinkSelf, myPublished, myResource, mySummary, myTitle, myUpdated, myDeletedByEmail, myDeletedByName, myDeletedComment);
		//@formatter:on
	}

	/**
	 * Sets the date/time that this entry was deleted.
	 */
	public void setDeleted(InstantDt theDeletedAt) {
		myDeletedAt = theDeletedAt;
	}

	public void setDeletedByEmail(StringDt theDeletedByEmail) {
		myDeletedByEmail = theDeletedByEmail;
	}

	public void setDeletedByName(StringDt theDeletedByName) {
		if (myDeletedByName == null) {
			myDeletedByName = new StringDt();
		}
		myDeletedByName = theDeletedByName;
	}

	public void setDeletedComment(StringDt theDeletedComment) {
		myDeletedComment = theDeletedComment;
	}

	public void setDeletedResourceId(StringDt theDeletedResourceId) {
		myDeletedResourceId = theDeletedResourceId;
	}

	public void setDeletedResourceType(CodeDt theDeletedResourceType) {
		myDeletedResourceType = theDeletedResourceType;
	}

	public void setDeletedResourceVersion(StringDt theDeletedResourceVersion) {
		myDeletedResourceVersion = theDeletedResourceVersion;
	}

	public void setLinkAlternate(StringDt theLinkAlternate) {
		myLinkAlternate = theLinkAlternate;
	}

	public void setLinkBase(StringDt theLinkBase) {
		myLinkBase = theLinkBase;
	}

	public void setLinkSearch(StringDt theLinkSearch) {
		myLinkSearch = theLinkSearch;
	}

	public void setLinkSelf(StringDt theLinkSelf) {
		if (myLinkSelf == null) {
			myLinkSelf = new StringDt();
		}
		myLinkSelf = theLinkSelf;
	}

	public void setPublished(InstantDt thePublished) {
		Validate.notNull(thePublished, "Published may not be null");
		myPublished = thePublished;
	}

	public void setResource(IResource theResource) {
		myResource = theResource;
	}

	public void setScore(DecimalDt theScore) {
		myScore = theScore;
	}

	public void setStatus(BoundCodeDt<BundleEntryStatusEnum> theStatus) {
		myStatus = theStatus;
	}

	/**
	 * @deprecated <b>DSTU2 Note:</b> As of DSTU2, bundle entries no longer have an updated time (this bit of metadata has been moved to the resource &lt;meta/&gt; element so it is redundant here). In
	 *             preparation for DSTU2, it is recommended that you migrate code away from using this method and over to using resource metadata instead.
	 */
	public void setUpdated(InstantDt theUpdated) {
		Validate.notNull(theUpdated, "Updated may not be null");
		myUpdated = theUpdated;
	}

	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		if (getResource() != null) {
			b.append("type", getResource().getClass().getSimpleName());
		} else {
			b.append("No resource");
		}
		b.append("id", getId());
		return b.toString();
	}

}
